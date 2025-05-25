package dev.quarris.fireandflames.world.inventory.menu;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.tool.ToolType;
import dev.quarris.fireandflames.data.tool.part.PartSlot;
import dev.quarris.fireandflames.data.tool.part.ToolPart;
import dev.quarris.fireandflames.data.tool.part.ToolParts;
import dev.quarris.fireandflames.setup.DataComponentSetup;
import dev.quarris.fireandflames.setup.MenuSetup;
import dev.quarris.fireandflames.setup.RegistrySetup;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringUtil;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class TinkersWorkbenchMenu extends AbstractContainerMenu {

    private static final ResourceLocation UPGRADE_TAB_NAME = ModRef.res("upgrade");
    private static final SimpleTab UPGRADE_TAB = SimpleTab.builder()
        .addSlot(container -> new NamedContainerSlot("tool", container, 0, 44, 69))
        .addSlot(container -> new NamedContainerSlot("catalyst", container, 1, 30, 91))
        .addSlot(container -> new NamedContainerSlot("catalyst", container, 2, 22, 65))
        .addSlot(container -> new NamedContainerSlot("catalyst", container, 3, 44, 47))
        .addSlot(container -> new NamedContainerSlot("catalyst", container, 4, 66, 65))
        .addSlot(container -> new NamedContainerSlot("catalyst", container, 5, 58, 91))
        .result((name, stacks) -> {
            ItemStack tool = stacks.get("tool").copy();
            tool.set(DataComponents.ITEM_NAME, name);
            return tool;
        })
        .build();

    private final SimpleContainer resultContainer = new SimpleContainer(1);
    private final ContainerLevelAccess access;
    private final Inventory playerInventory;

    private final Multimap<String, NamedSlot> inputSlots = ArrayListMultimap.create();

    private Consumer<String> nameChangedListener;
    private ITab currentTab = UPGRADE_TAB;
    private String toolName = "";

    public TinkersWorkbenchMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public TinkersWorkbenchMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(MenuSetup.TINKERS_WORKBENCH.get(), containerId);
        this.playerInventory = playerInventory;
        this.access = access;
        this.setTab(UPGRADE_TAB);
    }

    private void setTab(ITab tab) {
        Multimap<String, ItemStack> transferStacks = ArrayListMultimap.create();
        this.inputSlots.values().forEach(slot -> transferStacks.put(slot.getName(), slot.getItem()));

        this.inputSlots.clear();
        this.slots.clear();
        this.remoteSlots.clear();
        this.lastSlots.clear();


        tab.initTab(this::slotsChanged, this::addInputSlot);
        this.currentTab = tab;

        for (String name : new HashSet<>(transferStacks.keySet())) {
            Collection<ItemStack> stacks = transferStacks.get(name);
            Collection<NamedSlot> slots = this.inputSlots.get(name);

            Iterator<ItemStack> stackIterator = stacks.iterator();
            Iterator<NamedSlot> slotsIterator = slots.iterator();

            while (stackIterator.hasNext() && slotsIterator.hasNext()) {
                if (slotsIterator.next().safeInsert(stackIterator.next()).isEmpty()) {
                    stackIterator.remove();
                }
            }
        }

        transferContentsToInventory(this.playerInventory.player, transferStacks.values());

        ToolResultSlot resultSlot = new ToolResultSlot(this.resultContainer, 0, 138, 70, (player) -> this.access.execute((level, pos) -> this.currentTab.clearContainer()));
        tab.resultSlotIcon().ifPresent(icon -> resultSlot.setBackground(InventoryMenu.BLOCK_ATLAS, icon));
        this.addSlot(resultSlot);

        this.computeOutput(Component.empty());

        // Player Slots
        for (int slotY = 0; slotY < 3; slotY++) {
            for (int slotX = 0; slotX < 9; slotX++) {
                this.addSlot(new Slot(this.playerInventory, slotX + slotY * 9 + 9, 8 + slotX * 18, 137 + slotY * 18));
            }
        }

        for (int hotbar = 0; hotbar < 9; hotbar++) {
            this.addSlot(new Slot(this.playerInventory, hotbar, 8 + hotbar * 18, 195));
        }
    }

    public boolean setTabByName(ResourceLocation tabName) {
        if (UPGRADE_TAB_NAME.equals(tabName)) {
            this.setTab(UPGRADE_TAB);
            return true;
        }

        if (RegistrySetup.TOOL_TYPES.containsKey(tabName)) {
            this.setTab(ToolTab.of(RegistrySetup.TOOL_TYPES.get(tabName)));
            return true;
        }

        return false;
    }

    private static void transferContentsToInventory(Player player, Collection<ItemStack> stacks) {
        if (!player.isAlive() || player instanceof ServerPlayer && ((ServerPlayer) player).hasDisconnected()) {
            for (ItemStack stack : stacks) {
                player.drop(stack, false);
            }
        } else {
            for (ItemStack stack : stacks) {
                Inventory inventory = player.getInventory();
                if (player instanceof ServerPlayer) {
                    inventory.placeItemBackInInventory(stack);
                }
            }
        }
    }

    private void addInputSlot(NamedSlot slot) {
        this.inputSlots.put(slot.getName(), slot);
        this.addSlot(slot);
    }

    public Collection<NamedSlot> getInputSlots(String name) {
        return this.inputSlots.get(name);
    }

    @Override
    public void slotsChanged(Container container) {
        this.computeOutput(Component.literal(this.toolName));
    }

    public boolean setToolName(String itemName) {
        String validatedName = validateName(itemName);
        if (validatedName != null && !validatedName.equals(this.toolName)) {
            this.toolName = validatedName;
            this.computeOutput(Component.literal(this.toolName));
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    private static String validateName(String itemName) {
        String s = StringUtil.filterText(itemName);
        return s.length() <= 50 ? s : null;
    }

    private void computeOutput(Component toolName) {
        ItemStack result = this.currentTab.createResult(toolName, slotName -> this.inputSlots.get(slotName).stream().map(NamedSlot::getItem).toList());
        this.access.execute((level, pos) -> this.resultContainer.setItem(0, result));
        if (this.nameChangedListener != null) {
            String name = result.getHoverName().getString();
            if (result.isEmpty()) {
                name = "";
            }
            this.nameChangedListener.accept(name);
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, pos) -> this.currentTab.moveContentsToInventory(player));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public void setNameChangedListener(Consumer<String> onNameChanged) {
        this.nameChangedListener = onNameChanged;
    }

    public interface ITab {

        Container getContainer();

        void initTab(ContainerListener listener, Consumer<NamedSlot> inputSlots);

        ItemStack createResult(Component name, IStackAccessor stackAccessor);

        default Optional<ResourceLocation> resultSlotIcon() {
            return Optional.empty();
        }

        default void moveContentsToInventory(Player player) {
            if (this.getContainer() == null) return;

            if (!player.isAlive() || player instanceof ServerPlayer && ((ServerPlayer) player).hasDisconnected()) {
                for (int j = 0; j < this.getContainer().getContainerSize(); j++) {
                    player.drop(this.getContainer().removeItemNoUpdate(j), false);
                }
            } else {
                for (int i = 0; i < this.getContainer().getContainerSize(); i++) {
                    Inventory inventory = player.getInventory();
                    if (player instanceof ServerPlayer) {
                        inventory.placeItemBackInInventory(this.getContainer().removeItemNoUpdate(i));
                    }
                }
            }
        }

        default void clearContainer() {
            this.getContainer().clearContent();
        }
    }

    public static class SimpleTab implements ITab {

        private final List<INamedSlotFactory> slotFactories;
        private final BiFunction<Component, IStackAccessor, ItemStack> resultFactory;
        protected SimpleContainer container;

        private SimpleTab(List<INamedSlotFactory> slotFactories, BiFunction<Component, IStackAccessor, ItemStack> resultFactory) {
            this.slotFactories = slotFactories;
            this.resultFactory = resultFactory;
        }

        public static SimpleTabBuilder builder() {
            return new SimpleTabBuilder();
        }

        @Override
        public void initTab(ContainerListener listener, Consumer<NamedSlot> inputSlots) {
            this.container = new SimpleContainer(this.slotFactories.size());
            this.container.addListener(listener);

            for (INamedSlotFactory factory : this.slotFactories) {
                inputSlots.accept(factory.createSlot(this.container));
            }
        }

        @Override
        public ItemStack createResult(Component name, IStackAccessor stackAccessor) {
            return this.resultFactory.apply(name, stackAccessor);
        }

        @Override
        public SimpleContainer getContainer() {
            return this.container;
        }

        public static class SimpleTabBuilder {

            private final List<INamedSlotFactory> slotFactories = new ArrayList<>();
            private BiFunction<Component, IStackAccessor, ItemStack> resultFactory = (name, container) -> ItemStack.EMPTY;

            private SimpleTabBuilder() {
            }

            public SimpleTabBuilder addSlot(INamedSlotFactory slotFactory) {
                this.slotFactories.add(slotFactory);
                return this;
            }

            public SimpleTabBuilder result(BiFunction<Component, IStackAccessor, ItemStack> resultFactory) {
                this.resultFactory = resultFactory;
                return this;
            }

            public SimpleTab build() {
                return new SimpleTab(Collections.unmodifiableList(this.slotFactories), this.resultFactory);
            }
        }
    }

    public static class ToolTab implements ITab {

        private final ToolType<?> toolType;
        private final ResourceLocation toolTypeIcon;

        private SimpleContainer container;

        public static ToolTab of(ToolType<?> type) {
            return new ToolTab(type);
        }

        public ToolTab(ToolType<?> toolType) {
            this.toolType = toolType;
            this.toolTypeIcon = RegistrySetup.TOOL_TYPES.getKey(this.toolType).withPrefix("item/slot/");
        }

        @Override
        public void initTab(ContainerListener listener, Consumer<NamedSlot> inputSlots) {
            Collection<PartSlot> partSlots = this.toolType.getAllSlots();
            this.container = new SimpleContainer(partSlots.size());
            this.container.addListener(listener);

            int slot = 0;
            for (PartSlot partSlot : partSlots) {
                SlotPosition slotPosition = partSlot.slotPos();
                NamedSlot namedSlot = new NamedContainerSlot(partSlot.name(), this.container, slot, slotPosition.x(), slotPosition.y()) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return stack.has(DataComponentSetup.TOOL_PART) && stack.get(DataComponentSetup.TOOL_PART).type().is(partSlot.type());
                    }
                };
                namedSlot.setBackground(TextureAtlas.LOCATION_BLOCKS, partSlot.type().getKey().location().withPrefix("item/slot/"));
                inputSlots.accept(namedSlot);
                slot++;
            }
        }

        @Override
        public ItemStack createResult(Component name, IStackAccessor stackAccessor) {
            ToolParts.Builder partBuilder = ToolParts.builder();
            for (PartSlot partSlot : this.toolType.getAllSlots()) {
                ItemStack stack = stackAccessor.get(partSlot.name());
                ToolPart part = ToolPart.fromStack(stack).orElse(null);
                if (part == null) {
                    return ItemStack.EMPTY;
                }

                partBuilder.add(partSlot.name(), part);
            }

            ItemStack stack = this.toolType.buildFrom(partBuilder.build(this.toolType.mainPartName()));
            if (!StringUtil.isBlank(name.getString())) {
                stack.set(DataComponents.ITEM_NAME, name);
            }
            return stack;
        }

        @Override
        public SimpleContainer getContainer() {
            return this.container;
        }

        @Override
        public Optional<ResourceLocation> resultSlotIcon() {
            return Optional.ofNullable(this.toolTypeIcon);
        }
    }

    @FunctionalInterface
    public interface INamedSlotFactory {
        NamedSlot createSlot(Container container);
    }

    @FunctionalInterface
    public interface IStackAccessor {
        Collection<ItemStack> getAll(String name);

        default ItemStack get(String name) {
            Iterator<ItemStack> iterator = this.getAll(name).iterator();
            return iterator.hasNext() ? iterator.next() : ItemStack.EMPTY;
        }
    }
}

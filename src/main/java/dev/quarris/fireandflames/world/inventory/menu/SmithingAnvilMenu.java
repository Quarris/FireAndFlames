package dev.quarris.fireandflames.world.inventory.menu;

import dev.quarris.fireandflames.data.tool.material.ToolMaterial;
import dev.quarris.fireandflames.data.tool.material.IMaterialHolder;
import dev.quarris.fireandflames.setup.MenuSetup;
import dev.quarris.fireandflames.setup.RecipeSetup;
import dev.quarris.fireandflames.util.MenuHelper;
import dev.quarris.fireandflames.world.inventory.crafting.SmithingAnvilRecipe;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SmithingAnvilMenu extends AbstractContainerMenu {

    private final SimpleContainer slotContainer = new SimpleContainer(1);
    private RecipeHolder<SmithingAnvilRecipe> currentRecipe;
    private final Inventory playerInventory;

    public SmithingAnvilMenu(int containerId, Inventory playerInventory) {
        this(MenuSetup.SMITHING_ANVIL.get(), containerId, playerInventory);
    }

    public SmithingAnvilMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory) {
        super(menuType, containerId);
        this.playerInventory = playerInventory;
        this.slotContainer.addListener(this::slotsChanged);

        this.addSlot(new Slot(this.slotContainer, 0, 80, 66) {
            @Override
            public int getMaxStackSize(ItemStack stack) {
                return 1;
            }
        });

        MenuHelper.addPlayerSlots(playerInventory, 8, 104, this::addSlot);
    }

    @Override
    public void slotsChanged(Container container) {
        Level level = this.playerInventory.player.level();
        this.currentRecipe = level.getRecipeManager().getRecipeFor(RecipeSetup.SMITHING_ANVIL_TYPE.get(), new SmithingAnvilRecipe.Input(this.slotContainer.getItem(0)), level).orElse(null);

        super.slotsChanged(container);
    }

    public RecipeHolder<SmithingAnvilRecipe> getRecipe() {
        return this.currentRecipe;
    }

    public boolean onHammerHit(int index) {
        if (this.getRecipe() == null || index < 0 || index >= this.getRecipe().value().possibleOutputs().size()) return false;

        if (!this.playerInventory.player.level().isClientSide()) {
            Holder<ToolMaterial> material = null;
            if (this.slotContainer.getItem(0).getItem() instanceof IMaterialHolder materialHolder) {
                material = materialHolder.getMaterial(this.slotContainer.getItem(0));
            }

            this.slotContainer.setItem(0, this.getRecipe().value().createOutputs(material).get(index));
        }

        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack returnStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            returnStack = slotStack.copy();
            if (slotIndex != 0) {
                if (this.hasRecipe(player.level(), slotStack)) {
                    if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotIndex >= 1 && slotIndex < 28) {
                    if (!this.moveItemStackTo(slotStack, 28, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotIndex >= 28 && slotIndex < 37 && !this.moveItemStackTo(slotStack, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, 1, 37, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == returnStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }

        return returnStack;
    }

    private boolean hasRecipe(Level level, ItemStack stack) {
        return level.getRecipeManager().getRecipeFor(RecipeSetup.SMITHING_ANVIL_TYPE.get(), new SmithingAnvilRecipe.Input(stack), level).isPresent();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.isAlive() || player instanceof ServerPlayer && ((ServerPlayer) player).hasDisconnected()) {
            for (int j = 0; j < this.slotContainer.getContainerSize(); j++) {
                player.drop(this.slotContainer.removeItemNoUpdate(j), false);
            }
        } else {
            for (int i = 0; i < this.slotContainer.getContainerSize(); i++) {
                Inventory inventory = player.getInventory();
                if (player instanceof ServerPlayer) {
                    inventory.placeItemBackInInventory(this.slotContainer.removeItemNoUpdate(i));
                }
            }
        }
    }
}

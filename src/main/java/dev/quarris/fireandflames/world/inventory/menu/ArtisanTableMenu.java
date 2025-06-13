package dev.quarris.fireandflames.world.inventory.menu;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.network.payload.ArtisanTableSetOutputsS2CPayload;
import dev.quarris.fireandflames.setup.MenuSetup;
import dev.quarris.fireandflames.setup.RecipeSetup;
import dev.quarris.fireandflames.util.MenuHelper;
import dev.quarris.fireandflames.world.inventory.crafting.ArtisanRecipeOutput;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class ArtisanTableMenu extends AbstractContainerMenu {

    private final Container inputContainer = new SimpleContainer(1);
    private final Container outputContainer = new SimpleContainer(2);
    private final Inventory playerInventory;

    private final List<ArtisanRecipeOutput> outputs = new ArrayList<>();
    private boolean recipeLocked;
    private int outputSelection;

    public ArtisanTableMenu(int containerId, Inventory playerInventory) {
        this(MenuSetup.ARTISAN_TABLE.get(), containerId, playerInventory);
    }

    public ArtisanTableMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory) {
        super(menuType, containerId);
        this.playerInventory = playerInventory;

        this.addSlot(new Slot(this.inputContainer, 0, 44, 24) {
            @Override
            public void set(ItemStack stack) {
                ArtisanTableMenu.this.updateRecipes(stack);
                super.set(stack);
            }
        });

        this.addSlot(new ResultSlot(this.outputContainer, 0, 98, 24));
        this.addSlot(new ResultSlot(this.outputContainer, 1, 116, 24));

        MenuHelper.addPlayerSlots(playerInventory, 8, 102, this::addSlot);
    }

    public void onRecipeCrafted() {
        if (!this.recipeLocked) {
            int requiredInput = this.outputs.get(this.outputSelection).requiredCount();
            this.inputContainer.removeItem(0, requiredInput);
        }

        if (!this.outputContainer.isEmpty()) {
            this.recipeLocked = true;
            return;
        }

        this.recipeLocked = false;
        this.updateRecipes(this.inputContainer.getItem(0));
    }

    public void updateRecipes(ItemStack inputStack) {
        Level level = this.playerInventory.player.level();

        if (inputStack.isEmpty()) {
            this.clearRecipe();
            return;
        }

        if (this.recipeLocked || level.isClientSide()) {
            return;
        }

        SingleRecipeInput input = new SingleRecipeInput(inputStack);
        List<ArtisanRecipeOutput> allRecipes = Stream.of(
            level.getRecipeManager().getRecipesFor(RecipeSetup.MATERIAL_ARTISAN_CRAFTING_TYPE.get(), input, level).stream()
                .map(RecipeHolder::value)
                .map(recipe -> recipe.createResults(input, level.registryAccess()))
                .flatMap(Collection::stream),
            level.getRecipeManager().getRecipesFor(RecipeSetup.ARTISAN_CRAFTING_TYPE.get(), input, level).stream()
                .map(RecipeHolder::value)
                .map(recipe -> recipe.createResults(input, level.registryAccess())),
            level.getRecipeManager().getRecipesFor(RecipeSetup.FAMILY_ARTISAN_CRAFTING_TYPE.get(), input, level).stream()
                .map(RecipeHolder::value)
                .map(recipe -> recipe.createResults(input, level.registryAccess()))
                .flatMap(Collection::stream)
        ).flatMap(s -> s).toList();

        this.setPossibleOutputs(allRecipes);
        PacketDistributor.sendToPlayer((ServerPlayer) this.playerInventory.player, new ArtisanTableSetOutputsS2CPayload(new ArrayList<>(this.outputs)));
    }

    public void setPossibleOutputs(List<ArtisanRecipeOutput> outputs) {
        int oldSelection = this.outputSelection;
        this.clearRecipe();
        this.outputs.addAll(outputs);
        this.selectOutput(Mth.clamp(oldSelection, 0, outputs.size() - 1));
        ModRef.LOGGER.info("{} at {}", outputs, this.playerInventory.player.level().getGameTime());
    }

    public List<ArtisanRecipeOutput> getOutputs() {
        return Collections.unmodifiableList(this.outputs);
    }

    public void clearRecipe() {
        this.outputs.clear();
        this.outputSelection = 0;
        if (!this.recipeLocked) {
            this.outputContainer.setItem(0, ItemStack.EMPTY);
            this.outputContainer.setItem(1, ItemStack.EMPTY);
        }
    }

    public void selectOutput(int outputSelectionIndex) {
        if (this.outputs.isEmpty()) {
            return;
        }
        this.outputSelection = (outputSelectionIndex + this.outputs.size()) % this.outputs.size();

        if (!this.recipeLocked) {
            ArtisanRecipeOutput output = this.outputs.get(this.outputSelection);
            this.outputContainer.setItem(0, output.main().copy());
            this.outputContainer.setItem(1, output.byproduct().copy());
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack returnStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            returnStack = slotStack.copy();
            if (slotIndex == 1 || slotIndex == 2) {
                if (!this.moveItemStackTo(slotStack, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                ItemStack otherResultStack = this.slots.get(-(slotIndex - 1) + 2).getItem();
                if (!otherResultStack.isEmpty() && !this.moveItemStackTo(otherResultStack, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(slotStack, returnStack);
            } else if (slotIndex != 0) {
                if (this.hasRecipe(player.level(), slotStack)) {
                    if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotIndex >= 3 && slotIndex < 30) {
                    if (!this.moveItemStackTo(slotStack, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotIndex >= 30 && slotIndex < 39 && !this.moveItemStackTo(slotStack, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, 3, 39, false)) {
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

    public boolean hasRecipe(Level level, ItemStack stack) {
        SingleRecipeInput input = new SingleRecipeInput(stack);
        return !level.getRecipeManager().getRecipesFor(RecipeSetup.ARTISAN_CRAFTING_TYPE.get(), input, level).isEmpty()
            || !level.getRecipeManager().getRecipesFor(RecipeSetup.MATERIAL_ARTISAN_CRAFTING_TYPE.get(), input, level).isEmpty()
            || !level.getRecipeManager().getRecipesFor(RecipeSetup.FAMILY_ARTISAN_CRAFTING_TYPE.get(), input, level).isEmpty();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.isAlive() || player instanceof ServerPlayer && ((ServerPlayer) player).hasDisconnected()) {
            player.drop(this.inputContainer.removeItemNoUpdate(0), false);
            if (this.recipeLocked) {
                for (int j = 0; j < this.outputContainer.getContainerSize(); j++) {
                    player.drop(this.outputContainer.removeItemNoUpdate(j), false);
                }
            }
        } else {
            Inventory inventory = player.getInventory();
            if (player instanceof ServerPlayer) {
                inventory.placeItemBackInInventory(this.inputContainer.removeItemNoUpdate(0));
                if (this.recipeLocked) {
                    for (int i = 0; i < this.outputContainer.getContainerSize(); i++) {
                        inventory.placeItemBackInInventory(this.outputContainer.removeItemNoUpdate(i));
                    }
                }
            }
        }
    }

    public int getOutputSelection() {
        return this.outputSelection;
    }

    public class ResultSlot extends Slot {

        public ResultSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            ArtisanTableMenu.this.onRecipeCrafted();
            super.onTake(player, stack);
        }
    }
}

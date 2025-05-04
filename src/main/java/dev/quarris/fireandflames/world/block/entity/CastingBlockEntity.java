package dev.quarris.fireandflames.world.block.entity;

import dev.quarris.fireandflames.world.crucible.crafting.CastingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public abstract class CastingBlockEntity<T extends CastingRecipe> extends BlockEntity {

    private ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot > 2) return false;

            if (!CastingBlockEntity.this.tank.isEmpty()) {
                return false;
            }

            return this.getStackInSlot(-slot + 1).isEmpty();
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!CastingBlockEntity.this.tank.isEmpty()) return stack;
            if (slot != 0) return stack;
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!CastingBlockEntity.this.tank.isEmpty()) return ItemStack.EMPTY;
            if (slot != 1) return ItemStack.EMPTY;
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        protected void onContentsChanged(int slot) {
            CastingBlockEntity.this.setChanged();
        }
    };

    private final FluidTank tank = new FluidTank(0, stack -> this.getLevel().getRecipeManager().getRecipeFor(this.getRecipeType(), new CastingRecipe.Input(stack, this.inventory.getStackInSlot(0)), this.getLevel()).isPresent()) {

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            RecipeHolder<T> recipe = CastingBlockEntity.this.recipe;
            if (recipe == null) {
                // Set recipe based on input
                recipe = CastingBlockEntity.this.getLevel().getRecipeManager().getRecipeFor(CastingBlockEntity.this.getRecipeType(), new CastingRecipe.Input(resource, CastingBlockEntity.this.inventory.getStackInSlot(0)), CastingBlockEntity.this.getLevel()).orElse(null);
            }

            if (recipe == null) {
                return 0;
            }

            // Update capacity
            this.capacity = recipe.value().getFluidInput().amount();

            int filled = super.fill(resource, action);
            if (action.simulate()) {
                // Reset recipe and capacity
                this.capacity = 0;
            } else if (filled > 0) {
                CastingBlockEntity.this.recipe = recipe;
            }

            return filled;
        }

        @Override
        public FluidTank setCapacity(int capacity) {
            super.setCapacity(capacity);
            CastingBlockEntity.this.setChanged();
            return this;
        }

        @Override
        public CompoundTag writeToNBT(HolderLookup.Provider pLookup, CompoundTag pTag) {
            if (this.capacity > 0) {
                pTag.putInt("Capacity", this.capacity);
            }

            return super.writeToNBT(pLookup, pTag);
        }

        @Override
        public FluidTank readFromNBT(HolderLookup.Provider pLookup, CompoundTag pTag) {
            if (pTag.contains("Capacity")) {
                this.setCapacity(pTag.getInt("Capacity"));
            }

            return super.readFromNBT(pLookup, pTag);
        }

        @Override
        protected void onContentsChanged() {
            if (this.isEmpty()) {
                CastingBlockEntity.this.recipe = null;
                this.setCapacity(0);
            }

            CastingBlockEntity.this.setChanged();
        }
    };

    private RecipeHolder<T> recipe;
    private int coolingTicks;

    protected CastingBlockEntity(BlockEntityType<? extends CastingBlockEntity> type, BlockPos pPos, BlockState pState) {
        super(type, pPos, pState);
    }

    public static <T extends CastingRecipe> void serverTick(Level pLevel, BlockPos pPos, BlockState pState, CastingBlockEntity<T> pBasin) {
        if (pBasin.recipe == null) {
            pBasin.coolingTicks = 0;
            return;
        }

        T recipe = pBasin.recipe.value();
        if (pBasin.tank.getFluid().getAmount() < recipe.getFluidInput().amount()) {
            pBasin.coolingTicks = 0;
            pBasin.recipe = null;
            return;
        }

        pBasin.coolingTicks++;
        pBasin.setChanged();
        if (pBasin.coolingTicks >= recipe.coolingTime) {
            if (recipe.consumesItem()) {
                pBasin.getInventory().setStackInSlot(0, ItemStack.EMPTY);
            }

            pBasin.getInventory().setStackInSlot(1, recipe.getResult().copy());
            pBasin.tank.setFluid(FluidStack.EMPTY);
            pBasin.recipe = null;
        }
    }

    public int getSlotWithItem() {
        if (!this.getInventory().getStackInSlot(1).isEmpty()) {
            return 1;
        }

        if (!this.getInventory().getStackInSlot(0).isEmpty()) {
            return 0;
        }

        return -1;
    }

    public ItemStackHandler getInventory() {
        return this.inventory;
    }

    public FluidTank getTank() {
        return this.tank;
    }

    public RecipeHolder<T> getRecipe() {
        return this.recipe;
    }

    public int getCoolingTicks() {
        return this.coolingTicks;
    }

    public abstract RecipeType<T> getRecipeType();

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.getLevel() != null) {
            this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 0);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.put("Inventory", this.inventory.serializeNBT(pRegistries));
        pTag.put("Tank", this.tank.writeToNBT(pRegistries, new CompoundTag()));
        pTag.putInt("CoolingTicks", this.coolingTicks);
        if (this.recipe != null) {
            pTag.putString("RecipeId", this.recipe.id().toString());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        this.inventory.deserializeNBT(pRegistries, pTag.getCompound("Inventory"));
        this.tank.readFromNBT(pRegistries, pTag.getCompound("Tank"));
        this.coolingTicks = pTag.getInt("CoolingTicks");
        this.recipe = null;
        if (pTag.contains("RecipeId")) {
            ResourceLocation recipeId = ResourceLocation.parse(pTag.getString("RecipeId"));
            var recipe = this.getLevel().getRecipeManager().byKey(recipeId);
            this.recipe = recipe.map(r -> (RecipeHolder<T>) r).orElse(null);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = super.getUpdateTag(pRegistries);
        saveAdditional(tag, pRegistries);
        return tag;
    }

    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}

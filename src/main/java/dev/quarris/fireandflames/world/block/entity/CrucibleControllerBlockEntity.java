package dev.quarris.fireandflames.world.block.entity;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.setup.DamageTypeSetup;
import dev.quarris.fireandflames.setup.RecipeSetup;
import dev.quarris.fireandflames.world.block.CrucibleControllerBlock;
import dev.quarris.fireandflames.world.crucible.CrucibleFluidTank;
import dev.quarris.fireandflames.world.crucible.CrucibleStructure;
import dev.quarris.fireandflames.world.crucible.crafting.EntityMeltingRecipe;
import dev.quarris.fireandflames.world.crucible.crafting.MeltingRecipeInput;
import dev.quarris.fireandflames.world.inventory.menu.CrucibleMenu;
import dev.quarris.fireandflames.setup.BlockEntitySetup;
import dev.quarris.fireandflames.world.crucible.crafting.CrucibleRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;
import java.util.Optional;

public class CrucibleControllerBlockEntity extends BlockEntity implements MenuProvider {

    public static final Component TITLE = Component.translatable("container.fireandflames.crucible.title");

    private CrucibleStructure crucibleStructure;

    private CrucibleFluidTank fluidTank;
    private ItemStackHandler inventory;
    private CrucibleRecipe.Active[] activeRecipes;
    private ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return CrucibleControllerBlockEntity.this.activeRecipes[index].getTicks();
        }

        @Override
        public void set(int index, int value) {

        }

        @Override
        public int getCount() {
            return CrucibleControllerBlockEntity.this.activeRecipes.length;
        }
    };

    // Crucible state
    private int currentHeat = 0;
    private int maxHeat = 800; // Base maximum heat level

    // Fluid storage information will be added later

    public CrucibleControllerBlockEntity(BlockPos pPos, BlockState pState) {
        super(BlockEntitySetup.CRUCIBLE_CONTROLLER.get(), pPos, pState);
        this.inventory = new ItemStackHandler(0);
        this.fluidTank = new CrucibleFluidTank(0);
        this.fluidTank.setListener(this::setChanged);
        this.activeRecipes = new CrucibleRecipe.Active[0];
    }

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, CrucibleControllerBlockEntity pCrucible) {
        if (pCrucible.getStructure() != null && pCrucible.getStructure().isInvalid()) {
            pCrucible.getStructure().getDrainPositions().forEach(drainPos -> pLevel.getBlockEntity(drainPos, BlockEntitySetup.CRUCIBLE_DRAIN.get()).ifPresent(drain -> drain.setCruciblePosition(null)));
            pCrucible.crucibleStructure = null;
            pCrucible.setChanged();
            return;
        }

        // Try forming structure
        if (pCrucible.getStructure() == null) {
            CrucibleStructure structure = CrucibleStructure.of(pCrucible.getLevel(), pCrucible.getBlockPos());
            // Structure valid
            boolean wasValid = pState.getValue(CrucibleControllerBlock.LIT);
            boolean valid = structure != null;

            if (valid) {
                pCrucible.crucibleStructure = structure;
                pCrucible.onStructureEstablished();
            }

            if (wasValid != valid) {
                pLevel.setBlock(pPos, pState.setValue(CrucibleControllerBlock.LIT, valid), Block.UPDATE_ALL);
            }

            return;
        }

        if (pCrucible.getStructure().isDirty()) {
            pCrucible.onStructureEstablished();
            pCrucible.getStructure().markClean();
        }

        // Tick recipes
        if (pCrucible.getInventory().getSlots() > 0) {
            for (int slot = 0; slot < pCrucible.getInventory().getSlots(); slot++) {
                CrucibleRecipe.Active recipe = pCrucible.activeRecipes[slot];
                if (recipe == null) {
                    recipe = new CrucibleRecipe.Active();
                    pCrucible.activeRecipes[slot] = recipe;
                    pCrucible.setChanged();
                }

                recipe.updateWith(pLevel, pCrucible.getInventory().getStackInSlot(slot));
                if (recipe.isFinished()) {
                    // If there is enough space to insert fluid
                    FluidStack output = recipe.createOutput();
                    // Only finalize the recipe if there is EXACTLY enough space for fluid.
                    if (pCrucible.getFluidTank().fill(output, IFluidHandler.FluidAction.SIMULATE) == output.getAmount()) {
                        pCrucible.getFluidTank().fill(output, IFluidHandler.FluidAction.EXECUTE);
                        pCrucible.getInventory().setStackInSlot(slot, recipe.createByproduct());
                        recipe.reset();
                    }
                }
            }
        }

        if (pLevel.getGameTime() % 20 == 0) {
            List<Entity> meltingEntities = pLevel.getEntities(null, pCrucible.getStructure().getInternalBounds());
            for (Entity entity : meltingEntities) {
                if (entity.hurt(pLevel.damageSources().source(DamageTypeSetup.CRUCIBLE_MELTING_DAMAGE), 1)) {
                    MeltingRecipeInput recipeInput = new MeltingRecipeInput(entity.getType(), pCrucible.getFluidTank().getStored() > 0);
                    pLevel.getRecipeManager().getRecipeFor(RecipeSetup.ENTITY_MELTING_TYPE.get(), recipeInput, pLevel).ifPresent(recipeHolder -> {
                        EntityMeltingRecipe recipe = recipeHolder.value();
                        pCrucible.getFluidTank().fill(recipe.result().createFluid(), IFluidHandler.FluidAction.EXECUTE); // Try fill regardless of state of tank
                    });

                }
            }
        }
    }



    private void setInventoryWithSize(int size) {
        CrucibleRecipe.Active[] newRecipes = new CrucibleRecipe.Active[size];
        if (this.activeRecipes != null) {
            System.arraycopy(this.activeRecipes, 0, newRecipes, 0, Math.min(this.activeRecipes.length, size));
        }
        this.activeRecipes = newRecipes;


        ItemStackHandler newInventory = new ItemStackHandler(size) {

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };

        // Copy old inventory to new, up to the new size
        // If the new inventory size has decreased, drop the items not able to be fitted in new inventory
        if (this.getInventory().getSlots() > 0) {
            BlockPos dropItemsPos = this.getBlockPos().relative(this.getBlockState().getValue(CrucibleControllerBlock.FACING));
            for (int slot = 0; slot < this.getInventory().getSlots(); slot++) {
                if (this.getLevel() != null && slot >= size) {
                    // If the inventory decreased in size, drop the overflowing items.
                    Containers.dropItemStack(this.getLevel(), dropItemsPos.getX(), dropItemsPos.getY(), dropItemsPos.getZ(), this.getInventory().getStackInSlot(slot));
                    continue;
                }

                newInventory.setStackInSlot(slot, this.getInventory().getStackInSlot(slot));
            }
        }

        this.inventory = newInventory;
    }

    private void setTankSize(int size) {
        this.getFluidTank().updateCapacity(size);
    }

    private void onStructureEstablished() {
        int size = this.getStructure().getInternalVolume();
        this.setInventoryWithSize(size);
        this.setTankSize(size * FluidType.BUCKET_VOLUME);
        if (this.getLevel() instanceof ServerLevel) {
            this.getStructure().getDrainPositions().forEach(drainPos -> {
                this.getLevel().getBlockEntity(drainPos, BlockEntitySetup.CRUCIBLE_DRAIN.get()).ifPresent(drain -> drain.setCruciblePosition(this.getBlockPos()));
            });
        }
        this.invalidateCapabilities();
        CrucibleStructure.ALL_CRUCIBLES.put(this.getBlockPos(), this.getStructure().getShape());
        this.setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.getLevel() != null) {
            this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 0);
        }
    }

    public CrucibleFluidTank getFluidTank() {
        return this.fluidTank;
    }

    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    public ItemStackHandler getInventory() {
        return this.inventory;
    }

    public CrucibleStructure getStructure() {
        return this.crucibleStructure;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);

        if (this.crucibleStructure != null) {
            CrucibleStructure.CODEC.encodeStart(NbtOps.INSTANCE, this.crucibleStructure)
                .resultOrPartial(ModRef.LOGGER::error)
                .ifPresent(nbt -> pTag.put("CrucibleStructure", nbt));
        }

        ListTag activeRecipes = new ListTag();
        for (int i = 0; i < this.activeRecipes.length; i++) {
            CrucibleRecipe.Active recipe = this.activeRecipes[i];
            if (recipe != null && recipe.hasRecipe()) {
                CompoundTag recipeTag = new CompoundTag();
                recipeTag.putInt("Slot", i);
                recipe.serializeNbt(recipeTag, pRegistries);
                activeRecipes.add(recipeTag);
            }
        }

        pTag.put("ActiveRecipes", activeRecipes);
        pTag.put("FluidTank", this.fluidTank.serializeNBT(pRegistries));
        pTag.put("Inventory", this.inventory.serializeNBT(pRegistries));
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);

        this.crucibleStructure = null;
        if (pTag.contains("CrucibleStructure")) {
            CrucibleStructure.CODEC.parse(NbtOps.INSTANCE, pTag.get("CrucibleStructure"))
                .resultOrPartial(ModRef.LOGGER::error)
                .ifPresent(structure -> {
                    CrucibleControllerBlockEntity.this.crucibleStructure = structure;
                    this.onStructureEstablished();
                });
        }

        ListTag activeRecipesTag = pTag.getList("ActiveRecipes", Tag.TAG_COMPOUND);
        activeRecipesTag.stream().map(tag -> ((CompoundTag) tag)).forEach(recipeTag -> {
            int slot = recipeTag.getInt("Slot");
            CrucibleRecipe.Active recipe = new CrucibleRecipe.Active();
            recipe.deserializeNbt(recipeTag, pRegistries);
            this.activeRecipes[slot] = recipe;
        });

        this.inventory.deserializeNBT(pRegistries, pTag.getCompound("Inventory"));
        this.fluidTank.deserializeNBT(pRegistries, pTag.getCompound("FluidTank"));

    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = super.getUpdateTag(pRegistries);
        saveAdditional(tag, pRegistries);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public Component getDisplayName() {
        return TITLE;
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInv, Player pPlayer) {
        return new CrucibleMenu(pContainerId, pPlayerInv, this, this.dataAccess);
    }
}
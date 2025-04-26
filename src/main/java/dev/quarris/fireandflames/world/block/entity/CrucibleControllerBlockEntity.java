package dev.quarris.fireandflames.world.block.entity;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.world.block.CrucibleControllerBlock;
import dev.quarris.fireandflames.world.crucible.CrucibleFluidTank;
import dev.quarris.fireandflames.world.crucible.CrucibleStructure;
import dev.quarris.fireandflames.world.inventory.menu.CrucibleMenu;
import dev.quarris.fireandflames.setup.BlockEntitySetup;
import dev.quarris.fireandflames.world.item.crafting.CrucibleRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.Arrays;

public class CrucibleControllerBlockEntity extends BlockEntity implements MenuProvider {

    public static final Component TITLE = Component.translatable("container.fireandflames.crucible.title");

    // Structure properties
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
        this.crucibleStructure = new CrucibleStructure(pPos);
        this.inventory = new ItemStackHandler(0);
        this.fluidTank = new CrucibleFluidTank(0);
        this.activeRecipes = new CrucibleRecipe.Active[0];
    }

    /**
     * Server-side tick handler for the crucible controller
     */
    public static void serverTick(Level level, BlockPos pos, BlockState state, CrucibleControllerBlockEntity crucible) {
        crucible.crucibleStructure.setLevel(level);

        // Check structure
        if (!crucible.crucibleStructure.isStructureValid()) {
            crucible.crucibleStructure = new CrucibleStructure(pos);
            crucible.crucibleStructure.setLevel(level);
            boolean wasValid = state.getValue(CrucibleControllerBlock.LIT);
            boolean valid = crucible.crucibleStructure.validateCrucibleStructure();
            if (valid) {
                crucible.onStructureEstablished();
            }

            if (wasValid != valid) {
                level.setBlock(pos, state.setValue(CrucibleControllerBlock.LIT, valid), Block.UPDATE_ALL);
            }
            return;
        }

        // Tick recipes
        if (crucible.inventory.getSlots() > 0) {
            for (int slot = 0; slot < crucible.inventory.getSlots(); slot++) {
                CrucibleRecipe.Active recipe = crucible.activeRecipes[slot];
                if (recipe == null) {
                    recipe = new CrucibleRecipe.Active();
                    crucible.activeRecipes[slot] = recipe;
                    crucible.setChanged();
                }

                recipe.updateWith(level, crucible.inventory.getStackInSlot(slot));
                if (recipe.isFinished()) {
                    // If there is enough space to insert fluid
                    FluidStack output = recipe.createOutput();
                    // Only finalize the recipe if there is EXACTLY enough space for fluid.
                    if (crucible.fluidTank.fill(output, IFluidHandler.FluidAction.SIMULATE) == output.getAmount()) {
                        crucible.fluidTank.fill(output, IFluidHandler.FluidAction.EXECUTE);
                        crucible.inventory.setStackInSlot(slot, recipe.createByproduct());
                        recipe.reset();
                    }
                }
            }
        }
    }

    private ItemStackHandler createInventory(int size) {
        this.activeRecipes = new CrucibleRecipe.Active[size];
        return new ItemStackHandler(size) {

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return this.getStackInSlot(slot).isEmpty();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
    }

    private CrucibleFluidTank createTank(int size) {
        return new CrucibleFluidTank(size) {
            @Override
            public void onContentsChanged() {
                CrucibleControllerBlockEntity.this.setChanged();
            }
        };
    }

    private void onStructureEstablished() {
        int size = this.crucibleStructure.getVolume();
        this.inventory = this.createInventory(size);
        this.fluidTank = this.createTank(size);
        this.invalidateCapabilities();
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

    public CrucibleRecipe.Active getRecipeAt(int slot) {
        if (slot >= 0 && slot < this.activeRecipes.length) {
            return this.activeRecipes[slot];
        }

        return null;
    }

    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        CrucibleStructure.CODEC.encodeStart(NbtOps.INSTANCE, this.crucibleStructure)
            .resultOrPartial(ModRef.LOGGER::error)
            .ifPresent(nbt -> pTag.put("CrucibleStructure", nbt));

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

        CrucibleStructure.CODEC.parse(NbtOps.INSTANCE, pTag.get("CrucibleStructure"))
            .resultOrPartial(ModRef.LOGGER::error)
            .ifPresent(structure -> CrucibleControllerBlockEntity.this.crucibleStructure = structure);

        if (this.crucibleStructure.isStructureValid()) {
            this.onStructureEstablished();
        }

        this.invalidateCapabilities();

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

    // Getter methods
    public boolean isStructureValid() {
        return this.crucibleStructure.isStructureValid();
    }

    public void invalidate() {
        this.crucibleStructure.invalidate();
    }

    public ItemStackHandler getInventory() {
        return this.inventory;
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
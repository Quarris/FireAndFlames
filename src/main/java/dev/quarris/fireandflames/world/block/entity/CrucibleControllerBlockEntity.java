package dev.quarris.fireandflames.world.block.entity;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.config.ServerConfigs;
import dev.quarris.fireandflames.setup.BlockEntitySetup;
import dev.quarris.fireandflames.setup.CapabilitySetup;
import dev.quarris.fireandflames.setup.DamageTypeSetup;
import dev.quarris.fireandflames.setup.RecipeSetup;
import dev.quarris.fireandflames.util.recipe.IFluidOutput;
import dev.quarris.fireandflames.world.block.CrucibleControllerBlock;
import dev.quarris.fireandflames.world.crucible.CrucibleFluidTank;
import dev.quarris.fireandflames.world.crucible.CrucibleStructure;
import dev.quarris.fireandflames.world.crucible.crafting.AlloyingRecipe;
import dev.quarris.fireandflames.world.crucible.crafting.CrucibleRecipe;
import dev.quarris.fireandflames.world.crucible.crafting.EntityMeltingRecipe;
import dev.quarris.fireandflames.world.crucible.fuel.ActiveFuel;
import dev.quarris.fireandflames.world.crucible.fuel.IFuelProvider;
import dev.quarris.fireandflames.world.inventory.menu.CrucibleMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.*;

public class CrucibleControllerBlockEntity extends BlockEntity implements MenuProvider {

    public static final Component TITLE = Component.translatable("container.fireandflames.crucible.title");


    private CrucibleStructure crucibleStructure;
    private final CrucibleFluidTank fluidTank;
    private ItemStackHandler inventory;
    private CrucibleRecipe.Active[] activeRecipes;

    private final ContainerData recipeDataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return CrucibleControllerBlockEntity.this.activeRecipes[index].getProgressPercent();
        }

        @Override
        public void set(int index, int value) {

        }

        @Override
        public int getCount() {
            return CrucibleControllerBlockEntity.this.activeRecipes.length;
        }
    };

    private final Lazy<List<BlockCapabilityCache<IFuelProvider, Direction>>> fuelProviders = Lazy.of(() -> this.getStructure().getFuelPositions().stream().map(pos -> BlockCapabilityCache.create(CapabilitySetup.FUEL_PROVIDER, ((ServerLevel) this.getLevel()), pos, null)).toList());
    private final List<ActiveFuel> activeFuels = new ArrayList<>();
    private int burnTicks;
    private int heat;

    public CrucibleControllerBlockEntity(BlockPos pPos, BlockState pState) {
        super(BlockEntitySetup.CRUCIBLE_CONTROLLER.get(), pPos, pState);
        this.inventory = new ItemStackHandler(0);
        this.fluidTank = new CrucibleFluidTank(0);
        this.fluidTank.setListener(this::setChanged);
        this.activeRecipes = new CrucibleRecipe.Active[0];
    }

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, CrucibleControllerBlockEntity pCrucible) {
        CrucibleStructure structure = pCrucible.getStructure();
        if (structure != null && structure.isInvalid()) {
            structure.getDrainPositions().forEach(drainPos -> pLevel.getBlockEntity(drainPos, BlockEntitySetup.CRUCIBLE_DRAIN.get()).ifPresent(drain -> drain.setCruciblePosition(null)));
            pCrucible.crucibleStructure = null;
            pCrucible.setChanged();
            return;
        }

        // Try forming structure
        if (structure == null) {
            structure = CrucibleStructure.of(pLevel, pPos);
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

        if (structure.isDirty()) {
            pCrucible.onStructureEstablished();
            structure.markClean();
        }

        // Fuel consumption
        int baseTemp = getBaseBiomeTemperature(pLevel.getBiome(pPos).value(), pLevel.dimensionType().ultraWarm());
        int maxHeat = pCrucible.burnTicks > 0 ? Math.max(pCrucible.heat, baseTemp) : baseTemp;

        if (!ServerConfigs.isHeatEnabled()) {
            maxHeat = Integer.MAX_VALUE; // Set the crucible to the heat of the sun if heat is disabled
        }

        if (pCrucible.burnTicks <= 0) {
            pCrucible.activeFuels.clear();
            var fuelProviders = pCrucible.fuelProviders.get().stream().map(BlockCapabilityCache::getCapability).filter(Objects::nonNull).toList();
            int burnTicks = 0;
            for (IFuelProvider fuelProvider : fuelProviders) {
                ActiveFuel activeFuel = fuelProvider.burn(baseTemp, pCrucible.activeFuels::contains);
                if (activeFuel.isEmpty()) continue;
                burnTicks += activeFuel.burnValue();
                int heat = activeFuel.heat();
                if (heat > maxHeat) {
                    maxHeat = heat;
                }

                pCrucible.activeFuels.add(activeFuel);
            }

            pCrucible.burnTicks = burnTicks;
            pCrucible.setChanged();
        }

        if (pCrucible.heat != maxHeat) {
            pCrucible.heat = maxHeat;
            pCrucible.setChanged();
        }

        // Smelting recipes
        ItemStackHandler inventory = pCrucible.getInventory();
        if (inventory.getSlots() > 0) {
            for (int slot = 0; slot < inventory.getSlots(); slot++) {
                CrucibleRecipe.Active recipe = pCrucible.activeRecipes[slot];
                if (recipe.updateWith(pLevel, new CrucibleRecipe.Input(inventory.getStackInSlot(slot), pCrucible.heat))) {
                    pCrucible.burnTicks--;

                    if (recipe.isFinished()) {
                        // If there is enough space to insert fluid
                        FluidStack output = recipe.createOutput();
                        // Only finalize the recipe if there is enough space for the output fluid.
                        if (pCrucible.getFluidTank().fill(output, IFluidHandler.FluidAction.SIMULATE) == output.getAmount()) {
                            pCrucible.getFluidTank().fill(output, IFluidHandler.FluidAction.EXECUTE);
                            inventory.setStackInSlot(slot, recipe.createByproduct());
                            recipe.reset();
                        }
                    }
                }
            }
        }

        // Entity Melting
        if (pLevel.getGameTime() % 20 == 0) {
            List<Entity> meltingEntities = pLevel.getEntities((Entity) null, structure.getInternalBounds(), e -> !(e instanceof ItemEntity));
            for (Entity entity : meltingEntities) {
                if (entity.hurt(pLevel.damageSources().source(DamageTypeSetup.CRUCIBLE_MELTING_DAMAGE), 1)) {
                    EntityMeltingRecipe.Input recipeInput = new EntityMeltingRecipe.Input(entity.getType(), pCrucible.getFluidTank().getStored() > 0, pCrucible.heat);
                    pLevel.getRecipeManager().getRecipeFor(RecipeSetup.ENTITY_MELTING_TYPE.get(), recipeInput, pLevel).ifPresent(recipeHolder -> {
                        EntityMeltingRecipe recipe = recipeHolder.value();
                        pCrucible.getFluidTank().fill(recipe.result().createFluid(), IFluidHandler.FluidAction.EXECUTE); // Try fill regardless of state of tank
                    });
                    pCrucible.burnTicks -= 10;
                }
            }
        }

        // Alloying Recipes
        if (pCrucible.fluidTank.getTanks() > 1) {
            AlloyingRecipe.Input recipeInput = new AlloyingRecipe.Input(pCrucible.fluidTank.getFluids(), pCrucible.heat);
            List<RecipeHolder<AlloyingRecipe>> recipes = pLevel.getRecipeManager().getRecipesFor(RecipeSetup.ALLOYING_TYPE.get(), recipeInput, pLevel);
            recipes.sort(Comparator.comparingInt(r -> r.value().ingredients().size()));
            if (!recipes.isEmpty()) {
                AlloyingRecipe recipe = recipes.getFirst().value();
                List<FluidStack> resultantAlloys = recipe.results().stream().map(IFluidOutput::createFluid).toList();
                List<FluidStack> drainStacks = pCrucible.fluidTank.canAlloy(resultantAlloys, recipe.ingredients());
                if (!drainStacks.isEmpty()) {
                    for (FluidStack drainStack : drainStacks) {
                        pCrucible.fluidTank.drain(drainStack, IFluidHandler.FluidAction.EXECUTE);
                    }

                    for (FluidStack resultantAlloy : resultantAlloys) {
                        pCrucible.fluidTank.fill(resultantAlloy, IFluidHandler.FluidAction.EXECUTE);
                    }
                    pCrucible.burnTicks -= 10;
                }
            }
        }

        pCrucible.setChanged();
    }


    private void setInventoryWithSize(int size) {
        CrucibleRecipe.Active[] newRecipes = new CrucibleRecipe.Active[size];
        for (int i = 0, len = newRecipes.length; i < len; i++)
            newRecipes[i] = new CrucibleRecipe.Active();

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
            this.fuelProviders.invalidate();
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

    public List<ActiveFuel> getActiveFuels() {
        return this.activeFuels;
    }

    public int getHeat() {
        return this.heat;
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

        ListTag activeFuels = new ListTag();
        for (ActiveFuel fuel : this.activeFuels) {
            if (fuel == null) continue;
            activeFuels.add(fuel.saveTo(new CompoundTag(), pRegistries));
        }

        pTag.put("ActiveFuels", activeFuels);
        pTag.put("ActiveRecipes", activeRecipes);
        pTag.put("FluidTank", this.fluidTank.serializeNBT(pRegistries));
        pTag.put("Inventory", this.inventory.serializeNBT(pRegistries));
        pTag.putInt("Heat", this.heat);
        pTag.putInt("BurnTicks", this.burnTicks);
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

        ListTag activeFuelsTag = pTag.getList("ActiveFuels", Tag.TAG_COMPOUND);
        this.activeFuels.clear();
        activeFuelsTag.stream().map(tag -> ((CompoundTag) tag)).forEach(fuelTag -> {
            ActiveFuel fuel = ActiveFuel.load(fuelTag, pRegistries);
            if (fuel == null) return;
            this.activeFuels.add(fuel);
        });

        this.inventory.deserializeNBT(pRegistries, pTag.getCompound("Inventory"));
        this.fluidTank.deserializeNBT(pRegistries, pTag.getCompound("FluidTank"));
        this.heat = pTag.getInt("Heat");
        this.burnTicks = pTag.getInt("BurnTicks");
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
        return new CrucibleMenu(pContainerId, pPlayerInv, this, this.recipeDataAccess);
    }

    public static int getBaseBiomeTemperature(Biome biome, boolean ultraWarm) {
        if (!ServerConfigs.useBiomeTemperature()) return 0;

        double baseTemp = biome.getBaseTemperature() * ServerConfigs.getBaseTemperature();
        if (ultraWarm) {
            baseTemp *= ServerConfigs.getUltraWarmModifier();
        }

        return (int) Math.floor(baseTemp);
    }
}
package dev.quarris.fireandflames.world.crucible.fuel;

import dev.quarris.fireandflames.config.ServerConfigs;
import dev.quarris.fireandflames.data.maps.FuelData;
import dev.quarris.fireandflames.util.data.DataMapUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.Optional;

public class FluidHandlerFuelWrapper extends AbstractFuelProvider {

    private BlockCapabilityCache<IFluidHandler, Direction> fluidCache;

    public FluidHandlerFuelWrapper(ServerLevel level, BlockPos pos, Direction direction) {
        this.fluidCache = BlockCapabilityCache.create(Capabilities.FluidHandler.BLOCK, level, pos, direction);
    }

    public IFluidHandler handler() {
        return this.fluidCache.getCapability();
    }

    public boolean invalid() {
        return this.fluidCache == null || this.handler() == null;
    }

    @Override
    public ActiveFuel burn(int baseTemp, IFuelConflictChecker checker) {
        if (this.invalid()) return ActiveFuel.EMPTY;

        FluidStack toDrain = FluidStack.EMPTY;
        int maxHeat = 0;
        for (int tankSlot = 0; tankSlot < this.handler().getTanks(); tankSlot++) {
            FluidStack stack = this.handler().getFluidInTank(tankSlot).copyWithAmount(1);
            if (stack.isEmpty()) continue;
            FluidStack drained = this.handler().drain(stack, IFluidHandler.FluidAction.SIMULATE);
            if (drained.isEmpty() || checker.conflicts(new FluidActiveFuel(drained))) continue;

            Optional<Integer> fluidHeat = getFluidHeat(drained);
            if (fluidHeat.isEmpty()) continue;
            int temperature = fluidHeat.get();
            if (ServerConfigs.isHeatEnabled() && temperature < baseTemp) continue;

            if (temperature > maxHeat) {
                maxHeat = temperature;
                toDrain = drained;
            }
        }

        if (toDrain.isEmpty()) return ActiveFuel.EMPTY;

        FluidStack drained = this.handler().drain(toDrain, IFluidHandler.FluidAction.EXECUTE);
        if (drained.isEmpty()) return ActiveFuel.EMPTY;

        return new FluidActiveFuel(drained);
    }

    private static Optional<Integer> getFluidHeat(FluidStack stack) {
        Optional<Integer> fuelHeatData = DataMapUtil.getFuelData(stack.getFluid()).map(FuelData::heat);
        if (fuelHeatData.isEmpty()) {
            if (!ServerConfigs.useFluidTemperature()) return Optional.empty();
            return Optional.of(stack.getFluidType().getTemperature(stack));
        }

        return fuelHeatData;
    }

    public static final class FluidActiveFuel extends ActiveFuel {

        private final FluidStack stack;

        public FluidActiveFuel(FluidStack stack) {
            this.stack = stack;
        }

        @Override
        public int burnValue() {
            return DataMapUtil.getFuelData(this.stack().getFluid())
                .map(FuelData::burnTicks)
                .orElse(this.stack().getFluidType().getTemperature(this.stack()));
        }

        @Override
        public int heat() {
            return DataMapUtil.getFuelData(this.stack().getFluid())
                .map(FuelData::heat)
                .orElse(this.stack().getFluidType().getTemperature(this.stack()));
        }

        @Override
        public boolean matches(ActiveFuel other) {
            if (!(other instanceof FluidActiveFuel otherFuel)) {
                return false;
            }

            return FluidStack.isSameFluidSameComponents(this.stack(), otherFuel.stack());
        }

        @Override
        public CompoundTag saveTo(CompoundTag tag, HolderLookup.Provider provider) {
            super.saveTo(tag, provider);

            if (!this.stack.isEmpty()) {
                tag.put("Fluid", this.stack.save(provider));
            }

            return tag;
        }

        @Override
        public String type() {
            return "Fluid";
        }

        public static FluidActiveFuel load(CompoundTag tag, HolderLookup.Provider provider) {
            FluidStack fluid = FluidStack.EMPTY;
            if (tag.contains("Fluid")) {
                fluid = FluidStack.parse(provider, tag.getCompound("Fluid")).orElse(FluidStack.EMPTY);
            }

            return new FluidActiveFuel(fluid);
        }

        public FluidStack stack() {
            return stack;
        }

        @Override
        public String toString() {
            return "FluidActiveFuel[" +
                "stack=" + stack + ']';
        }
    }
}

package dev.quarris.fireandflames.util.data;

import dev.quarris.fireandflames.data.maps.FuelData;
import dev.quarris.fireandflames.setup.DataMapSetup;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

import java.util.Optional;

public class DataMapUtil {

    public static Optional<FuelData> getFuelData(Fluid fluid) {
        Holder<Fluid> fluidTypeHolder = BuiltInRegistries.FLUID.wrapAsHolder(fluid);
        FuelData fuelData = fluidTypeHolder.getData(DataMapSetup.FLUID_FUEL_DATA);
        return Optional.ofNullable(fuelData);
    }

    public static Optional<FuelData> getFuelData(Item item) {
        FuelData fuelData = BuiltInRegistries.ITEM.wrapAsHolder(item).getData(DataMapSetup.ITEM_FUEL_DATA);
        return Optional.ofNullable(fuelData);
    }

}

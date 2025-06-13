package dev.quarris.fireandflames.util.data;

import dev.quarris.fireandflames.data.map.ConverterData;
import dev.quarris.fireandflames.data.map.FuelData;
import dev.quarris.fireandflames.data.map.MaterialConversion;
import dev.quarris.fireandflames.data.tool.material.ToolMaterial;
import dev.quarris.fireandflames.setup.DataMapSetup;
import dev.quarris.fireandflames.setup.RegistrySetup;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;
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

    public static List<ConverterData> getConverters(Object stack, HolderLookup.Provider registries) {
        List<ConverterData> converterDataList = new ArrayList<>();
        for (Holder.Reference<ToolMaterial> mat : registries.lookupOrThrow(RegistrySetup.Keys.MATERIALS).listElements().toList()) {
            MaterialConversion conversion = mat.getData(DataMapSetup.MATERIAL_CONVERSIONS);
            if (conversion == null) continue;
            conversion.getMatchingConverters(stack).forEach(converter -> converterDataList.add(new ConverterData(mat, converter)));
        }

        return converterDataList;
    }
}

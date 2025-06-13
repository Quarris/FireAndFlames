package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.map.FuelData;
import dev.quarris.fireandflames.data.map.MaterialConversion;
import dev.quarris.fireandflames.data.tool.material.ToolMaterial;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

@EventBusSubscriber(modid = ModRef.ID, bus = EventBusSubscriber.Bus.MOD)
public class DataMapSetup {

    public static final DataMapType<Fluid, FuelData> FLUID_FUEL_DATA = DataMapType.builder(
        ModRef.res("fuel_data"), Registries.FLUID, FuelData.CODEC
    ).build();

    public static final DataMapType<Item, FuelData> ITEM_FUEL_DATA = DataMapType.builder(
        ModRef.res("fuel_data"), Registries.ITEM, FuelData.CODEC
    ).build();

    public static final DataMapType<ToolMaterial, MaterialConversion> MATERIAL_CONVERSIONS = DataMapType.builder(
        ModRef.res("conversions"), RegistrySetup.Keys.MATERIALS, MaterialConversion.CODEC
    ).synced(MaterialConversion.CODEC, true).build();

    @SubscribeEvent
    public static void registerDataMaps(RegisterDataMapTypesEvent event) {
        event.register(FLUID_FUEL_DATA);
        event.register(ITEM_FUEL_DATA);
        event.register(MATERIAL_CONVERSIONS);
    }

}

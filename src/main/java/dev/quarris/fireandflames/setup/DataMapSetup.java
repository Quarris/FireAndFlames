package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.maps.FuelData;
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

    @SubscribeEvent
    public static void registerDataMaps(RegisterDataMapTypesEvent event) {
        event.register(FLUID_FUEL_DATA);
        event.register(ITEM_FUEL_DATA);
    }

}

package dev.quarris.fireandflames.setup;

import com.mojang.serialization.MapCodec;
import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.map.FluidMaterialConverter;
import dev.quarris.fireandflames.data.map.IMaterialConverter;
import dev.quarris.fireandflames.data.map.ItemMaterialConverter;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MaterialConverterSetup {

    public static final DeferredRegister<MapCodec<? extends IMaterialConverter<?>>> REGISTRY = DeferredRegister.create(RegistrySetup.Keys.MATERIAL_CONVERTERS, ModRef.ID);

    public static final DeferredHolder<MapCodec<? extends IMaterialConverter<?>>, MapCodec<ItemMaterialConverter>> ITEM = REGISTRY.register("item", () -> ItemMaterialConverter.CODEC);
    public static final DeferredHolder<MapCodec<? extends IMaterialConverter<?>>, MapCodec<FluidMaterialConverter>> FLUID = REGISTRY.register("fluid", () -> FluidMaterialConverter.CODEC);


    public static void init(IEventBus modBus) {
        REGISTRY.register(modBus);
    }
}

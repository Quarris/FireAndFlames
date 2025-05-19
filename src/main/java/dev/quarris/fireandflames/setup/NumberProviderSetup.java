package dev.quarris.fireandflames.setup;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.config.number.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NumberProviderSetup {

    public static final DeferredRegister<MapCodec<? extends INumberProvider>> REGISTRY = DeferredRegister.create(RegistrySetup.Keys.NUMBER_PROVIDERS, ModRef.ID);

    public static final DeferredHolder<MapCodec<? extends INumberProvider>, MapCodec<ConstantNumber>> CONSTANT = REGISTRY.register("constant", () -> ConstantNumber.CODEC);
    public static final DeferredHolder<MapCodec<? extends INumberProvider>, MapCodec<AddNumber>> ADD = REGISTRY.register("ad", () -> AddNumber.CODEC);
    public static final DeferredHolder<MapCodec<? extends INumberProvider>, MapCodec<MultiplyNumber>> MULTIPLY = REGISTRY.register("multiply", () -> MultiplyNumber.CODEC);
    public static final DeferredHolder<MapCodec<? extends INumberProvider>, MapCodec<ConfigNumber>> CONFIG = REGISTRY.register("config", () -> ConfigNumber.CODEC);

    public static void init(IEventBus modBus) {
        REGISTRY.register(modBus);
    }
}

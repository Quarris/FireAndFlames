package dev.quarris.fireandflames.setup;

import com.mojang.serialization.MapCodec;
import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.config.number.INumberProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;

public class RegistrySetup {

    public static final Registry<MapCodec<? extends INumberProvider>> NUMBER_PROVIDERS = NumberProviderSetup.REGISTRY.makeRegistry(builder -> builder.defaultKey(NumberProviderSetup.CONSTANT.getKey()).sync(true));

    public static void init(IEventBus modBus) {

    }

    public static class Keys {
        public static final ResourceKey<Registry<MapCodec<? extends INumberProvider>>> NUMBER_PROVIDERS = ResourceKey.createRegistryKey(ModRef.res("number_providers"));
    }

}

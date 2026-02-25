package dev.quarris.fireandflames.setup;

import com.mojang.serialization.MapCodec;
import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.tool.modifier.effect.IToolEffect;
import dev.quarris.fireandflames.data.tool.modifier.effect.MiningSpeedEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ToolEffectSetup {

    public static final DeferredRegister<MapCodec<? extends IToolEffect>> REGISTRY = DeferredRegister.create(RegistrySetup.Keys.TOOL_EFFECTS, ModRef.ID);

    public static final DeferredHolder<MapCodec<? extends IToolEffect>, MapCodec<MiningSpeedEffect>> MINING_SPEED = REGISTRY.register("mining_speed", () -> MiningSpeedEffect.CODEC);

    public static void init(IEventBus modBus) {
        REGISTRY.register(modBus);
    }

}

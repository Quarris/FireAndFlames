package dev.quarris.fireandflames.setup;

import com.mojang.serialization.MapCodec;
import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.tool.modifier.ToolModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ToolModifierSetup {

    public static final DeferredRegister<MapCodec<ToolModifier>> REGISTRY = DeferredRegister.create(RegistrySetup.Keys.TOOL_MODIFIERS, ModRef.ID);

    public static void init(IEventBus modBus) {
        REGISTRY.register(modBus);
    }
}

package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.tool.part.PartType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PartTypeSetup {

    public static final DeferredRegister<PartType> REGISTRY = DeferredRegister.create(RegistrySetup.Keys.PART_TYPES, ModRef.ID);

    public static final DeferredHolder<PartType, PartType> PICKAXE_HEAD = REGISTRY.register("pickaxe_head", () -> new PartType());
    public static final DeferredHolder<PartType, PartType> AXE_HEAD = REGISTRY.register("axe_head", () -> new PartType());
    public static final DeferredHolder<PartType, PartType> HANDLE = REGISTRY.register("handle", () -> new PartType());
    public static final DeferredHolder<PartType, PartType> BINDING = REGISTRY.register("binding", () -> new PartType());

    public static void init(IEventBus modBus) {
        REGISTRY.register(modBus);
    }
}

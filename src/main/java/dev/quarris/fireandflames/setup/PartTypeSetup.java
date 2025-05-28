package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.tool.part.PartType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class PartTypeSetup {

    public static final DeferredRegister<PartType> REGISTRY = DeferredRegister.create(RegistrySetup.Keys.PART_TYPES, ModRef.ID);

    public static final DeferredHolder<PartType, PartType> PICKAXE_HEAD = REGISTRY.register("pickaxe_head", PartType::new);
    public static final DeferredHolder<PartType, PartType> AXE_HEAD = REGISTRY.register("axe_head", PartType::new);
    public static final DeferredHolder<PartType, PartType> SHOVEL_HEAD = REGISTRY.register("shovel_head", PartType::new);
    public static final DeferredHolder<PartType, PartType> HOE_HEAD = REGISTRY.register("hoe_head", PartType::new);
    public static final DeferredHolder<PartType, PartType> SWORD_BLADE = REGISTRY.register("sword_blade", PartType::new);
    public static final DeferredHolder<PartType, PartType> WIDE_GUARD = REGISTRY.register("wide_guard", PartType::new);
    public static final DeferredHolder<PartType, PartType> HANDLE = REGISTRY.register("handle", PartType::new);
    public static final DeferredHolder<PartType, PartType> BINDING = REGISTRY.register("binding", PartType::new);

    public static void init(IEventBus modBus) {
        REGISTRY.register(modBus);
    }
}

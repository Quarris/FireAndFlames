package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.tool.ToolType;
import dev.quarris.fireandflames.data.tool.part.CommonPartSlots;
import dev.quarris.fireandflames.world.inventory.menu.SlotPosition;
import dev.quarris.fireandflames.world.item.tool.AxeToolItem;
import dev.quarris.fireandflames.world.item.tool.PickaxeToolItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ToolTypeSetup {

    public static final DeferredRegister<ToolType<?>> REGISTRY = DeferredRegister.create(RegistrySetup.Keys.TOOL_TYPES, ModRef.ID);

    public static final DeferredHolder<ToolType<?>, ToolType<PickaxeToolItem>> PICKAXE = REGISTRY.register("pickaxe", () ->
        ToolType.of(ToolItemSetup.PICKAXE)
            .add(CommonPartSlots.PICKAXE_HEAD, CommonPartSlots.HANDLE, CommonPartSlots.GRIP)
            .ordering(0)
            .build(CommonPartSlots.PICKAXE_HEAD.name()));
    public static final DeferredHolder<ToolType<?>, ToolType<AxeToolItem>> AXE = REGISTRY.register("axe", () ->
        ToolType.of(ToolItemSetup.AXE)
            .add(CommonPartSlots.AXE_HEAD, CommonPartSlots.BINDING, CommonPartSlots.HANDLE.at(25, 88))
            .ordering(1)
            .build(CommonPartSlots.AXE_HEAD.name()));
    public static final DeferredHolder<ToolType<?>, ToolType<PickaxeToolItem>> HAMMER = REGISTRY.register("hammer", () ->
        ToolType.of(ToolItemSetup.HAMMER)
            .add(CommonPartSlots.HAMMER_LEFT, CommonPartSlots.HAMMER_RIGHT, CommonPartSlots.BINDING.at(63, 50), CommonPartSlots.HANDLE.at(44, 69), CommonPartSlots.GRIP.at(25, 88))
            .ordering(2)
            .build(CommonPartSlots.HAMMER_LEFT.name()));

    public static void init(IEventBus modBus) {
        REGISTRY.register(modBus);
    }
}

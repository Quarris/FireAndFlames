package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.tool.ToolType;
import dev.quarris.fireandflames.data.tool.part.CommonPartSlots;
import dev.quarris.fireandflames.world.item.tool.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.component.Tool;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ToolTypeSetup {

    public static final DeferredRegister<ToolType<?>> REGISTRY = DeferredRegister.create(RegistrySetup.Keys.TOOL_TYPES, ModRef.ID);

    public static final DeferredHolder<ToolType<?>, ToolType<PickaxeToolItem>> PICKAXE = REGISTRY.register("pickaxe", () ->
        ToolType.of(ToolItemSetup.PICKAXE)
            .ordering(0)
            .addPart(CommonPartSlots.PICKAXE_HEAD, 63, 50)
            .addPart(CommonPartSlots.HANDLE, 44, 69)
            .addPart(CommonPartSlots.GRIP, 25, 88)
            .durabilityModifier(1.0f).miningSpeed(1.0f).durabilityLossPerBlock(1)
            .addRule(Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_PICKAXE, 1.0f))
            .build(CommonPartSlots.PICKAXE_HEAD.name()));

    public static final DeferredHolder<ToolType<?>, ToolType<AxeToolItem>> AXE = REGISTRY.register("axe", () ->
        ToolType.of(ToolItemSetup.AXE)
            .ordering(1)
            .addPart(CommonPartSlots.AXE_HEAD, 63, 50)
            .addPart(CommonPartSlots.BINDING, 44, 69)
            .addPart(CommonPartSlots.HANDLE, 25, 88)
            .durabilityModifier(1.0f).miningSpeed(1.0f).damage(6.0f).attackSpeed(-3.2f).durabilityLossPerBlock(1)
            .addRule(Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_AXE, 1.0f))
            .build(CommonPartSlots.AXE_HEAD.name()));

    public static final DeferredHolder<ToolType<?>, ToolType<ShovelToolItem>> SHOVEL = REGISTRY.register("shovel", () ->
        ToolType.of(ToolItemSetup.SHOVEL)
            .ordering(1)
            .addPart(CommonPartSlots.SHOVEL_HEAD, 63, 50)
            .addPart(CommonPartSlots.BINDING, 44, 69)
            .addPart(CommonPartSlots.HANDLE, 25, 88)
            .durabilityModifier(1.0f).miningSpeed(1.0f).durabilityLossPerBlock(1)
            .addRule(Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_SHOVEL, 1.0f))
            .build(CommonPartSlots.SHOVEL_HEAD.name()));

    public static final DeferredHolder<ToolType<?>, ToolType<HoeToolItem>> HOE = REGISTRY.register("hoe", () ->
        ToolType.of(ToolItemSetup.HOE)
            .ordering(1)
            .addPart(CommonPartSlots.HOE_HEAD, 63, 50)
            .addPart(CommonPartSlots.HANDLE, 44, 69)
            .addPart(CommonPartSlots.GRIP, 25, 88)
            .durabilityModifier(1.0f).miningSpeed(1.0f).durabilityLossPerBlock(1)
            .addRule(Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_HOE, 1.0f))
            .build(CommonPartSlots.HOE_HEAD.name()));

    public static final DeferredHolder<ToolType<?>, ToolType<SwordToolItem>> SWORD = REGISTRY.register("sword", () ->
        ToolType.of(ToolItemSetup.SWORD)
            .ordering(1)
            .addPart(CommonPartSlots.SWORD_BLADE, 63, 50)
            .addPart(CommonPartSlots.WIDE_GUARD, 44, 69)
            .addPart(CommonPartSlots.HANDLE, 25, 88)
            .durabilityModifier(1.0f).miningSpeed(1.0f).damage(3.0f).attackSpeed(-2.4f).durabilityLossPerBlock(2)
            .addRule(Tool.Rule.overrideSpeed(BlockTags.SWORD_EFFICIENT, 1.5f))
            .build(CommonPartSlots.SWORD_BLADE.name()));

    public static final DeferredHolder<ToolType<?>, ToolType<PickaxeToolItem>> HAMMER = REGISTRY.register("hammer", () ->
        ToolType.of(ToolItemSetup.HAMMER)
            .ordering(2)
            .addPart(CommonPartSlots.HAMMER_LEFT.withIndex(3), 44, 46)
            .addPart(CommonPartSlots.HAMMER_RIGHT.withIndex(4), 67, 69)
            .addPart(CommonPartSlots.BINDING.withIndex(1), 63, 50)
            .addPart(CommonPartSlots.HANDLE, 44, 69)
            .addPart(CommonPartSlots.GRIP.withIndex(2), 25, 88)
            .durabilityModifier(5.0f).miningSpeed(0.8f).durabilityLossPerBlock(1)
            .addRule(Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_PICKAXE, 0.8f))
            .build(CommonPartSlots.HAMMER_LEFT.name()));

    public static void init(IEventBus modBus) {
        REGISTRY.register(modBus);
    }
}

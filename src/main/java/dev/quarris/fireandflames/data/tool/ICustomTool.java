package dev.quarris.fireandflames.data.tool;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.tool.material.ToolMaterial;
import dev.quarris.fireandflames.data.tool.part.PartSlot;
import dev.quarris.fireandflames.data.tool.part.ToolPart;
import dev.quarris.fireandflames.data.tool.part.ToolParts;
import dev.quarris.fireandflames.setup.DataComponentSetup;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.neoforged.neoforge.common.util.AttributeUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public interface ICustomTool {

    ResourceLocation BASE_ATTACK_DAMAGE_ATTRIBUTE_ID = ModRef.res("base_attack_damage");
    ResourceLocation BASE_ATTACK_SPEED_ATTRIBUTE_ID = ModRef.res("base_attack_speed");

    String TOOLTIP_PART_KEY = "tooltip." + ModRef.ID + ".tool.part";
    String TOOLTIP_STAT_KEY = "tooltip." + ModRef.ID + ".tool.stat";
    DecimalFormat STAT_FORMAT = new DecimalFormat("#.##");
    double PLAYER_BASE_ATTACK_DAMAGE = 1.0;
    double PLAYER_BASE_ATTACK_SPEED = 4.0;

    ToolType<? extends ICustomTool> getType();

    default ItemStack createFrom(ToolData data) {
        ItemStack stack = new ItemStack((Item) this);
        stack.set(DataComponentSetup.TOOL_DATA, data);
        this.onItemLoad(stack);
        return stack;
    }

    default ItemStack createFrom(Holder<ToolMaterial> material) {
        return this.createFrom(new ToolData(this.getType().createPartsFrom(material), List.of()));
    }

    default void onItemLoad(ItemStack stack) {
        ToolData toolData = stack.get(DataComponentSetup.TOOL_DATA);
        if (toolData == null || toolData.isEmpty()) return;
        ToolType<? extends ICustomTool> toolType = this.getType();
        Optional<ResolvedToolStats> resolved = ResolvedToolStats.resolve(toolType, toolData.toolParts());
        if (resolved.isEmpty()) return;
        ResolvedToolStats stats = resolved.get();
        ToolStats baseStats = toolType.baseStats();

        // Durability
        stack.set(DataComponents.MAX_DAMAGE, stats.durability());

        // Rules
        List<Tool.Rule> rules = new ArrayList<>();
        for (Tool.Rule rule : toolType.getRules()) {
            rules.add(new Tool.Rule(rule.blocks(), rule.speed().map(speed -> speed * stats.miningSpeed()), rule.correctForDrops()));
        }
        rules.add(Tool.Rule.deniesDrops(stats.deniesBlocks()));

        // Tool
        stack.set(DataComponents.TOOL, new Tool(rules, baseStats.miningSpeedModifier(), baseStats.durabilityLossPerBlock()));

        // Attributes
        ItemAttributeModifiers.Builder attributesBuilder = ItemAttributeModifiers.builder();
        attributesBuilder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(AttributeUtil.BASE_ATTACK_DAMAGE_ID, stats.attackDamage(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        attributesBuilder.add(Attributes.ATTACK_SPEED, new AttributeModifier(AttributeUtil.BASE_ATTACK_SPEED_ID, stats.attackSpeed(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        // TODO Custom tool attributes
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, attributesBuilder.build());

        // Modifiers
        toolData.modifiers().forEach(modifier -> modifier.modifyItem(stack));

        // Traits
    }

    /**
     * Lists each part's material, then each stat with the part(s) it comes from.
     */
    default void appendToolTooltip(ItemStack stack, List<Component> tooltip) {
        ToolData toolData = stack.get(DataComponentSetup.TOOL_DATA);
        if (toolData == null || toolData.isEmpty()) return;
        ToolType<? extends ICustomTool> toolType = this.getType();
        ToolParts parts = toolData.toolParts();

        List<PartSlot> slots = new ArrayList<>(toolType.getAllSlots());
        slots.sort(Comparator.comparingInt(PartSlot::index));
        for (PartSlot slot : slots) {
            ToolPart part = parts.getPart(slot.name());
            if (part == null || part.isEmpty()) continue;
            tooltip.add(Component.translatable(TOOLTIP_PART_KEY, slotName(slot), part.material().value().name()).withStyle(ChatFormatting.GRAY));
        }

        Optional<ResolvedToolStats> resolved = ResolvedToolStats.resolve(toolType, parts);
        if (resolved.isEmpty()) return;
        ResolvedToolStats stats = resolved.get();

        tooltip.add(statLine(stats, ToolStat.MINING_TIER, Component.literal(stats.tierMaterial().name())));
        tooltip.add(statLine(stats, ToolStat.MINING_SPEED, Component.literal(STAT_FORMAT.format(stats.miningSpeed()))));
        tooltip.add(statLine(stats, ToolStat.DURABILITY, Component.literal(String.valueOf(stats.durability()))));
        // Shown the way vanilla shows them, including the player's base attack damage and speed
        tooltip.add(statLine(stats, ToolStat.ATTACK_DAMAGE, Component.literal(STAT_FORMAT.format(stats.attackDamage() + PLAYER_BASE_ATTACK_DAMAGE))));
        tooltip.add(statLine(stats, ToolStat.ATTACK_SPEED, Component.literal(STAT_FORMAT.format(stats.attackSpeed() + PLAYER_BASE_ATTACK_SPEED))));
    }

    private static Component statLine(ResolvedToolStats stats, ToolStat stat, Component value) {
        MutableComponent sources = Component.empty();
        List<PartSlot> slots = stats.sources().get(stat);
        for (int i = 0; i < slots.size(); i++) {
            if (i > 0) sources.append(", ");
            sources.append(slotName(slots.get(i)));
        }
        return Component.translatable(TOOLTIP_STAT_KEY, Component.translatable(stat.getTranslationKey()), value, sources).withStyle(ChatFormatting.DARK_GREEN);
    }

    private static Component slotName(PartSlot slot) {
        return Component.translatable(Util.makeDescriptionId("tool_part.slot", ModRef.res(slot.name())));
    }
}

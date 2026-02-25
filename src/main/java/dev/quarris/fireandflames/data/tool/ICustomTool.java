package dev.quarris.fireandflames.data.tool;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.tool.material.ToolMaterial;
import dev.quarris.fireandflames.data.tool.part.ToolPart;
import dev.quarris.fireandflames.setup.DataComponentSetup;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.neoforged.neoforge.common.util.AttributeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ICustomTool {

    ResourceLocation BASE_ATTACK_DAMAGE_ATTRIBUTE_ID = ModRef.res("base_attack_damage");
    ResourceLocation BASE_ATTACK_SPEED_ATTRIBUTE_ID = ModRef.res("base_attack_speed");

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
        ToolStats toolStats = toolType.baseStats();

        float durabilityBonus = 0;
        float baseDurability = 0;
        float speed = 0;
        float speedBonus = 0;
        float damage = 0;
        float damageBonus = 0;
        float attackSpeed = 0;

        for (String partName : toolData.toolParts().partNames()) {
            ToolPart part = toolData.toolParts().getPart(partName);
            float partComposition = toolType.getSlot(partName).composition();
            ToolMaterial material = part.material().value();

            baseDurability += material.durability() * partComposition;
            durabilityBonus += material.durabilityBonus() * partComposition;

            speed += material.speed() * partComposition;
            speedBonus += material.speedBonus() * partComposition;

            damage += material.damageModifier() * partComposition;
            damageBonus += material.damageBonus() * partComposition;

            attackSpeed += material.attackSpeed() * partComposition;
        }

        // Durability
        int durability = (int) Math.ceil(baseDurability * toolStats.durabilityModifier()) + (int) durabilityBonus;
        stack.set(DataComponents.MAX_DAMAGE, Math.max(1, durability));

        // Rules
        List<Tool.Rule> rules = new ArrayList<>();
        for (Tool.Rule rule : toolType.getRules()) {
            rules.add(new Tool.Rule(rule.blocks(), rule.speed().isEmpty() ? Optional.empty() : Optional.of(rule.speed().get() * speed + speedBonus), rule.correctForDrops()));
        }
        rules.add(Tool.Rule.deniesDrops(toolData.toolParts().getPart(toolType.mainPartName()).material().value().deniesBlocks()));

        // Tool
        stack.set(DataComponents.TOOL, new Tool(rules, toolStats.miningSpeedModifier() + speedBonus, toolStats.durabilityLossPerBlock()));

        // Attributes
        ItemAttributeModifiers.Builder attributesBuilder = ItemAttributeModifiers.builder();
        attributesBuilder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(AttributeUtil.BASE_ATTACK_DAMAGE_ID, damage * toolStats.damage() + damageBonus, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        attributesBuilder.add(Attributes.ATTACK_SPEED, new AttributeModifier(AttributeUtil.BASE_ATTACK_SPEED_ID, toolStats.attackSpeed() + attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        // TODO Custom tool attributes
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, attributesBuilder.build());

        // Modifiers
        toolData.modifiers().forEach(modifier -> modifier.modifyItem(stack));

        // Traits
    }
}

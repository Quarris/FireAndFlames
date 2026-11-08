package dev.quarris.fireandflames.data.tool;

import dev.quarris.fireandflames.data.tool.material.ToolMaterial;
import dev.quarris.fireandflames.data.tool.part.PartSlot;
import dev.quarris.fireandflames.data.tool.part.ToolPart;
import dev.quarris.fireandflames.data.tool.part.ToolParts;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.*;
import java.util.function.ToDoubleFunction;

/**
 * The final stats of a tool, worked out from its parts.
 * <p>
 * Every stat comes from the material of the part(s) whose {@link dev.quarris.fireandflames.data.tool.part.PartType}
 * controls it. When several parts on one tool control the same stat, their values are averaged. The mining tier
 * cannot be averaged, so it comes from the main part if that controls it, otherwise from the lowest-index slot.
 * A stat that no part on the tool controls falls back to the main part.
 *
 * @param sources The slots each stat was taken from, in slot order.
 */
public record ResolvedToolStats(
    int durability,
    TagKey<Block> deniesBlocks,
    ToolMaterial tierMaterial,
    float miningSpeed,
    float attackDamage,
    float attackSpeed,
    Map<ToolStat, List<PartSlot>> sources
) {

    public static Optional<ResolvedToolStats> resolve(ToolType<?> toolType, ToolParts parts) {
        PartSlot mainSlot = toolType.getSlot(toolType.mainPartName());
        if (mainSlot == null || !hasPart(parts, mainSlot)) {
            return Optional.empty();
        }

        List<PartSlot> slots = new ArrayList<>(toolType.getAllSlots());
        slots.sort(Comparator.comparingInt(PartSlot::index));

        Map<ToolStat, List<PartSlot>> sources = new EnumMap<>(ToolStat.class);
        for (ToolStat stat : ToolStat.values()) {
            List<PartSlot> controlling = new ArrayList<>();
            for (PartSlot slot : slots) {
                if (slot.type().value().controls(stat) && hasPart(parts, slot)) {
                    controlling.add(slot);
                }
            }

            if (controlling.isEmpty()) {
                controlling.add(mainSlot);
            }
            sources.put(stat, Collections.unmodifiableList(controlling));
        }

        ToolStats base = toolType.baseStats();

        int durability = (int) Math.ceil(average(parts, sources.get(ToolStat.DURABILITY), ToolMaterial::durability) * base.durabilityModifier());
        float miningSpeed = (float) average(parts, sources.get(ToolStat.MINING_SPEED), ToolMaterial::speed);
        float attackDamage = base.damage() + (float) average(parts, sources.get(ToolStat.ATTACK_DAMAGE), ToolMaterial::damage);
        float attackSpeed = base.attackSpeed() + (float) average(parts, sources.get(ToolStat.ATTACK_SPEED), ToolMaterial::attackSpeed);

        List<PartSlot> tierSources = sources.get(ToolStat.MINING_TIER);
        PartSlot tierSlot = tierSources.contains(mainSlot) ? mainSlot : tierSources.getFirst();
        ToolMaterial tierMaterial = material(parts, tierSlot);

        return Optional.of(new ResolvedToolStats(
            Math.max(1, durability),
            tierMaterial.deniesBlocks(),
            tierMaterial,
            miningSpeed,
            attackDamage,
            attackSpeed,
            Collections.unmodifiableMap(sources)
        ));
    }

    private static double average(ToolParts parts, List<PartSlot> slots, ToDoubleFunction<ToolMaterial> stat) {
        double total = 0;
        for (PartSlot slot : slots) {
            total += stat.applyAsDouble(material(parts, slot));
        }
        return total / slots.size();
    }

    private static ToolMaterial material(ToolParts parts, PartSlot slot) {
        return parts.getPart(slot.name()).material().value();
    }

    private static boolean hasPart(ToolParts parts, PartSlot slot) {
        ToolPart part = parts.getPart(slot.name());
        return part != null && !part.isEmpty();
    }
}

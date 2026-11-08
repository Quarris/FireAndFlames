package dev.quarris.fireandflames.data.tool.part;

import dev.quarris.fireandflames.data.tool.ToolStat;
import dev.quarris.fireandflames.setup.RegistrySetup;
import net.minecraft.tags.TagKey;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class PartType {

    private final Set<ToolStat> controlledStats;

    /**
     * @param controlledStats The tool stats this part provides from its material.
     *                        A part that controls no stats only contributes traits.
     */
    public PartType(ToolStat... controlledStats) {
        EnumSet<ToolStat> stats = EnumSet.noneOf(ToolStat.class);
        Collections.addAll(stats, controlledStats);
        this.controlledStats = Collections.unmodifiableSet(stats);
    }

    public Set<ToolStat> controlledStats() {
        return this.controlledStats;
    }

    public boolean controls(ToolStat stat) {
        return this.controlledStats.contains(stat);
    }

    public boolean is(TagKey<PartType> tag) {
        return RegistrySetup.PART_TYPES.wrapAsHolder(this).is(tag);
    }

    @Override
    public String toString() {
        return RegistrySetup.PART_TYPES.wrapAsHolder(this).getRegisteredName();
    }

}

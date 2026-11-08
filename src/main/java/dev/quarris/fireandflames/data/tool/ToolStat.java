package dev.quarris.fireandflames.data.tool;

import dev.quarris.fireandflames.ModRef;

import java.util.Locale;

/**
 * A stat of a finished tool. Each {@link dev.quarris.fireandflames.data.tool.part.PartType} declares which of
 * these it controls, and the tool takes that stat from the material of the part(s) that control it.
 */
public enum ToolStat {
    DURABILITY,
    MINING_TIER,
    MINING_SPEED,
    ATTACK_DAMAGE,
    ATTACK_SPEED;

    public String getTranslationKey() {
        return "tool_stat." + ModRef.ID + "." + this.name().toLowerCase(Locale.ROOT);
    }
}

package dev.quarris.fireandflames.data.tool.part;

import net.minecraft.core.Holder;

public record PartSlot(int index, String name, Holder<PartType> type) {

    public PartSlot withIndex(int index) {
        return new PartSlot(index, this.name, this.type);
    }
}

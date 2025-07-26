package dev.quarris.fireandflames.data.tool.part;

import net.minecraft.core.Holder;

public record PartSlot(int index, String name, Holder<PartType> type, float composition) {

    public PartSlot withComposition(float composition) {
        return new PartSlot(this.index, this.name, this.type, composition);
    }

    public PartSlot withIndex(int index) {
        return new PartSlot(index, this.name, this.type, this.composition);
    }
}

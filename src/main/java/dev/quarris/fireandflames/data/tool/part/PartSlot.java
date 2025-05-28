package dev.quarris.fireandflames.data.tool.part;

import net.minecraft.core.Holder;

public record PartSlot(String name, Holder<PartType> type, float composition) {

    public PartSlot composition(float composition) {
        return new PartSlot(this.name, this.type, composition);
    }
}

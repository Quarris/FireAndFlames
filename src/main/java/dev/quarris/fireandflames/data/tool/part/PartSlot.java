package dev.quarris.fireandflames.data.tool.part;

import dev.quarris.fireandflames.world.inventory.menu.SlotPosition;
import net.minecraft.core.Holder;

public record PartSlot(String name, Holder<PartType> type, SlotPosition slotPos) {

    public PartSlot(String name, Holder<PartType> type) {
        this(name, type, new SlotPosition());
    }

    public PartSlot at(int x, int y) {
        return new PartSlot(this.name, this.type, new SlotPosition(x, y));
    }

    public PartSlot named(String name) {
        return new PartSlot(name, this.type, this.slotPos);
    }
}

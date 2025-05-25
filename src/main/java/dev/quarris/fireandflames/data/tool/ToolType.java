package dev.quarris.fireandflames.data.tool;

import dev.quarris.fireandflames.data.tool.part.PartSlot;
import dev.quarris.fireandflames.data.tool.part.PartType;
import dev.quarris.fireandflames.data.tool.part.ToolPart;
import dev.quarris.fireandflames.data.tool.part.ToolParts;
import dev.quarris.fireandflames.world.inventory.menu.SlotPosition;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.Supplier;

public final class ToolType<T extends ICustomTool> {

    private final Supplier<T> toolItem;
    private final String mainPartName;
    private final Map<String, PartSlot> slots;
    private final int ordering;

    private ToolType(Supplier<T> toolItem, String mainPartName, Map<String, PartSlot> slots, int ordering) {
        this.toolItem = toolItem;
        this.mainPartName = mainPartName;
        this.slots = slots;
        this.ordering = ordering;
    }

    public ItemStack buildFrom(ToolParts parts) {
        if (!this.partsValid(parts)) return ItemStack.EMPTY;

        return this.toolItem.get().createFrom(new ToolData(parts));
    }

    public boolean partsValid(ToolParts parts) {
        for (Map.Entry<String, PartSlot> entry : this.slots.entrySet()) {
            ToolPart part = parts.getPart(entry.getKey());
            if (part == null) return false;
            if (!part.type().is(entry.getValue().type())) {
                return false;
            }
        }

        return true;
    }

    public ToolParts createPartsFrom(ToolMaterial material) {
        ToolParts.Builder builder = ToolParts.builder();

        for (PartSlot slot : this.slots.values()) {
            builder.add(slot.name(), new ToolPart(slot.type(), material));
        }

        return builder.build(this.mainPartName);
    }

    public Collection<PartSlot> getAllSlots() {
        return this.slots.values();
    }

    public static <T extends ICustomTool> Builder<T> of(Supplier<T> toolItem) {
        return new Builder<>(toolItem);
    }

    public String mainPartName() {
        return mainPartName;
    }

    public Map<String, PartSlot> slots() {
        return slots;
    }

    public int ordering() {
        return ordering;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ToolType<?>) obj;
        return Objects.equals(this.mainPartName, that.mainPartName) &&
            Objects.equals(this.slots, that.slots) &&
            this.ordering == that.ordering;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mainPartName, slots, ordering);
    }

    @Override
    public String toString() {
        return "ToolType[" +
            "mainPartName=" + mainPartName + ", " +
            "slots=" + slots + ", " +
            "ordering=" + ordering + ']';
    }


    public static class Builder<T extends ICustomTool> {

        private final Supplier<T> toolItem;
        private final Map<String, PartSlot> parts = new Object2ObjectArrayMap<>();
        private int ordering = -1;

        private Builder(Supplier<T> toolItem) {
            this.toolItem = toolItem;
        }

        public Builder<T> add(PartSlot... slots) {
            for (PartSlot slot : slots) {
                this.add(slot);
            }
            return this;
        }

        public Builder<T> add(PartSlot slot) {
            if (this.parts.containsKey(slot.name())) {
                throw new IllegalArgumentException("Duplicate part slot with name " + slot.name());
            }
            this.parts.put(slot.name(), slot);
            return this;
        }

        public Builder<T> add(String id, Holder<PartType> partType, int slotX, int slotY) {
            return this.add(new PartSlot(id, partType, new SlotPosition(slotX, slotY)));
        }

        public Builder<T> ordering(int ordering) {
            this.ordering = ordering;
            return this;
        }

        public ToolType<T> build(String mainPartId) {
            if (!this.parts.containsKey(mainPartId)) {
                throw new IllegalStateException("ToolType does not contain the correct main part " + mainPartId);
            }

            return new ToolType<>(this.toolItem, mainPartId, Collections.unmodifiableMap(this.parts), this.ordering);
        }
    }

}

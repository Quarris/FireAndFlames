package dev.quarris.fireandflames.data.tool;

import dev.quarris.fireandflames.data.tool.part.PartSlot;
import dev.quarris.fireandflames.data.tool.part.PartType;
import dev.quarris.fireandflames.data.tool.part.ToolPart;
import dev.quarris.fireandflames.data.tool.part.ToolParts;
import dev.quarris.fireandflames.world.inventory.menu.PartPosition;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;

import java.util.*;
import java.util.function.Supplier;

public final class ToolType<T extends ICustomTool> {

    private final Supplier<T> toolItem;
    private final String mainPartName;
    private final Map<String, PartSlot> parts;
    private final Map<PartSlot, PartPosition> slots;
    private final int ordering;
    private final Set<Tool.Rule> rules;
    private final ToolStats baseStats;

    private ToolType(Supplier<T> toolItem, String mainPartName, Map<String, PartSlot> parts, Map<PartSlot, PartPosition> slots, int ordering, ToolStats stats, Set<Tool.Rule> rules) {
        this.toolItem = toolItem;
        this.mainPartName = mainPartName;
        this.parts = parts;
        this.slots = slots;
        this.ordering = ordering;
        this.baseStats = stats;
        this.rules = rules;
    }

    public ItemStack buildFrom(ToolParts parts) {
        if (!this.partsValid(parts)) return ItemStack.EMPTY;

        return this.toolItem.get().createFrom(new ToolData(parts));
    }

    public boolean partsValid(ToolParts parts) {
        for (Map.Entry<String, PartSlot> entry : this.parts.entrySet()) {
            ToolPart part = parts.getPart(entry.getKey());
            if (part == null) return false;
            if (!part.type().is(entry.getValue().type())) {
                return false;
            }
        }

        return true;
    }

    public ToolParts createPartsFrom(Holder<ToolMaterial> material) {
        ToolParts.Builder builder = ToolParts.builder();

        for (PartSlot slot : this.parts.values()) {
            builder.add(slot.name(), new ToolPart(slot.type(), material));
        }

        return builder.build(this.mainPartName);
    }

    public Collection<PartSlot> getAllSlots() {
        return this.parts.values();
    }

    public static <T extends ICustomTool> Builder<T> of(Supplier<T> toolItem) {
        return new Builder<>(toolItem);
    }

    public Set<Tool.Rule> getRules() {
        return this.rules;
    }

    public String mainPartName() {
        return mainPartName;
    }

    public Map<String, PartSlot> parts() {
        return parts;
    }

    public PartPosition getPartPosition(PartSlot part) {
        return this.slots.get(part);
    }

    public int ordering() {
        return ordering;
    }

    public ToolStats baseStats() {
        return this.baseStats;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ToolType<?>) obj;
        return Objects.equals(this.mainPartName, that.mainPartName) &&
            Objects.equals(this.parts, that.parts) &&
            this.ordering == that.ordering;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mainPartName, parts, ordering);
    }

    @Override
    public String toString() {
        return "ToolType[" +
            "mainPartName=" + mainPartName + ", " +
            "slots=" + parts + ", " +
            "ordering=" + ordering + ']';
    }

    public PartSlot getSlot(String slotName) {
        return this.parts().get(slotName);
    }


    public static class Builder<T extends ICustomTool> {

        private final Supplier<T> toolItem;
        private final Map<String, PartSlot> parts = new Object2ObjectArrayMap<>();
        private final Map<PartSlot, PartPosition> slots = new Object2ObjectArrayMap<>();
        private int ordering = -1;
        private final ToolStats.Builder stats = new ToolStats.Builder();
        private final Set<Tool.Rule> rules = new HashSet<>();

        private Builder(Supplier<T> toolItem) {
            this.toolItem = toolItem;
        }

        public Builder<T> addPart(PartSlot slot) {
            if (this.parts.containsKey(slot.name())) {
                throw new IllegalArgumentException("Duplicate part slot with name " + slot.name());
            }
            this.parts.put(slot.name(), slot);
            return this;
        }

        public Builder<T> addRule(Tool.Rule rule) {
            this.rules.add(rule);
            return this;
        }

        public Builder<T> addPart(PartSlot slot, int slotX, int slotY) {
            return this.addPart(slot, new PartPosition(slotX, slotY));
        }

        public Builder<T> addPart(String name, Holder<PartType> partType, float affect, int slotX, int slotY) {
            return this.addPart(name, partType, affect, new PartPosition(slotX, slotY));
        }

        public Builder<T> addPart(String name, Holder<PartType> partType, float affect, PartPosition position) {
            return this.addPart(new PartSlot(name, partType, affect), position);
        }

        public Builder<T> addPart(PartSlot slot, PartPosition position) {
            this.parts.put(slot.name(), slot);
            this.slots.put(slot, position);
            return this;
        }


        public Builder<T> ordering(int ordering) {
            this.ordering = ordering;
            return this;
        }

        public Builder<T> durabilityModifier(float durabilityModifier) {
            this.stats.durabilityModifier(durabilityModifier);
            return this;
        }

        public Builder<T> durabilityLossPerBlock(int loss) {
            this.stats.durabilityLossPerBlock(loss);
            return this;
        }

        public Builder<T> miningSpeed(float speed) {
            this.stats.miningSpeedModifier(speed);
            return this;
        }

        public Builder<T> damage(float damage) {
            this.stats.damage(damage);
            return this;
        }

        public Builder<T> attackSpeed(float attackSpeed) {
            this.stats.baseAttackSpeed(attackSpeed);
            return this;
        }

        public ToolType<T> build(String mainPartId) {
            if (!this.parts.containsKey(mainPartId)) {
                throw new IllegalStateException("ToolType does not contain the correct main part " + mainPartId);
            }

            return new ToolType<>(this.toolItem, mainPartId, Collections.unmodifiableMap(this.parts), Collections.unmodifiableMap(this.slots), this.ordering, this.stats.build(), this.rules);
        }
    }

}

package dev.quarris.fireandflames.world.crucible.fuel;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public abstract class ActiveFuel {
    public static final ActiveFuel EMPTY = new ActiveFuel() {
        @Override
        public int burnValue() {
            return 0;
        }

        @Override
        public int heat() {
            return 0;
        }

        @Override
        public boolean matches(ActiveFuel other) {
            return other.isEmpty();
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public String type() {
            return "Empty";
        }
    };

    public abstract int burnValue();

    public abstract int heat();

    public abstract boolean matches(ActiveFuel other);

    public boolean isEmpty() {
        return this == EMPTY || this.burnValue() <= 0;
    }

    public abstract String type();

    public CompoundTag saveTo(CompoundTag tag, HolderLookup.Provider lookup) {
        tag.putString("Type", this.type());
        return tag;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ActiveFuel otherFuel)) return false;
        if (this.isEmpty() && otherFuel.isEmpty()) return true;
        return this.matches(otherFuel) && otherFuel.matches(this);
    }

    public static ActiveFuel load(CompoundTag tag, HolderLookup.Provider lookup) {
        String type = tag.getString("Type");
        return switch (type) {
            case "Fluid" -> FluidHandlerFuelWrapper.FluidActiveFuel.load(tag, lookup);
            case "Item" -> ItemHandlerFuelWrapper.ItemActiveFuel.load(tag, lookup);
            default -> EMPTY;
        };
    }
}

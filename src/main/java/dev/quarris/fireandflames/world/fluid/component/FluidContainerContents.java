package dev.quarris.fireandflames.world.fluid.component;

import com.google.common.collect.Iterables;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public final class FluidContainerContents {
    private static final int NO_SLOT = -1;
    private static final int MAX_SIZE = 256;
    public static final FluidContainerContents EMPTY = new FluidContainerContents(NonNullList.create());
    public static final Codec<FluidContainerContents> CODEC = Slot.CODEC
        .sizeLimitedListOf(MAX_SIZE)
        .xmap(FluidContainerContents::fromSlots, FluidContainerContents::asSlots);
    
    public static final StreamCodec<RegistryFriendlyByteBuf, FluidContainerContents> STREAM_CODEC = FluidStack.OPTIONAL_STREAM_CODEC
        .apply(ByteBufCodecs.list(MAX_SIZE))
        .map(FluidContainerContents::new, p_331691_ -> p_331691_.fluids);
    
    private final NonNullList<FluidStack> fluids;
    private final int hashCode;

    private FluidContainerContents(NonNullList<FluidStack> fluids) {
        if (fluids.size() > MAX_SIZE) {
            throw new IllegalArgumentException("Got " + fluids.size() + " fluids, but maximum is MAX_SIZE");
        } else {
            this.fluids = fluids;

            int hash = 0;
            for (FluidStack fluidStack : fluids) {
                hash = hash * 31 + FluidStack.hashFluidAndComponents(fluidStack);
            }
            
            this.hashCode = hash;
        }
    }

    private FluidContainerContents(int size) {
        this(NonNullList.withSize(size, FluidStack.EMPTY));
    }

    private FluidContainerContents(List<FluidStack> fluids) {
        this(fluids.size());

        for (int i = 0; i < fluids.size(); i++) {
            this.fluids.set(i, fluids.get(i));
        }
    }

    private static FluidContainerContents fromSlots(List<Slot> slots) {
        OptionalInt optionalint = slots.stream().mapToInt(Slot::index).max();
        if (optionalint.isEmpty()) {
            return EMPTY;
        } else {
            FluidContainerContents fluidcontainercontents = new FluidContainerContents(optionalint.getAsInt() + 1);

            for (Slot fluidcontainercontents$slot : slots) {
                fluidcontainercontents.fluids.set(fluidcontainercontents$slot.index(), fluidcontainercontents$slot.fluid());
            }

            return fluidcontainercontents;
        }
    }

    public static FluidContainerContents fromFluids(List<FluidStack> fluids) {
        int i = findLastNonEmptySlot(fluids);
        if (i == NO_SLOT) {
            return EMPTY;
        } else {
            FluidContainerContents contents = new FluidContainerContents(i + 1);

            for (int j = 0; j <= i; j++) {
                contents.fluids.set(j, fluids.get(j).copy());
            }

            return contents;
        }
    }

    private static int findLastNonEmptySlot(List<FluidStack> fluids) {
        for (int i = fluids.size() - 1; i >= 0; i--) {
            if (!fluids.get(i).isEmpty()) {
                return i;
            }
        }

        return NO_SLOT;
    }

    private List<Slot> asSlots() {
        List<Slot> list = new ArrayList<>();

        for (int i = 0; i < this.fluids.size(); i++) {
            FluidStack fluidstack = this.fluids.get(i);
            if (!fluidstack.isEmpty()) {
                list.add(new Slot(i, fluidstack));
            }
        }

        return list;
    }

    public void consume(BiConsumer<Integer, FluidStack> consumer) {
        for (int slot = 0; slot < this.getSlots(); slot++) {
            consumer.accept(slot, this.getStackInSlot(slot));
        }
    }

    public FluidStack copyOne() {
        return this.fluids.isEmpty() ? FluidStack.EMPTY : this.fluids.get(0).copy();
    }

    public Stream<FluidStack> stream() {
        return this.fluids.stream().map(FluidStack::copy);
    }

    public Stream<FluidStack> nonEmptyStream() {
        return this.fluids.stream().filter(p_331322_ -> !p_331322_.isEmpty()).map(FluidStack::copy);
    }

    public Iterable<FluidStack> nonEmptyFluids() {
        return Iterables.filter(this.fluids, p_331420_ -> !p_331420_.isEmpty());
    }

    public Iterable<FluidStack> nonEmptyFluidsCopy() {
        return Iterables.transform(this.nonEmptyFluids(), FluidStack::copy);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else {
            return other instanceof FluidContainerContents contents && listMatches(this.fluids, contents.fluids);
        }
    }

    @Deprecated
    public static boolean listMatches(List<FluidStack> list, List<FluidStack> other) {
        if (list.size() != other.size()) {
            return false;
        } else {
            for (int i = 0; i < list.size(); i++) {
                if (!FluidStack.matches(list.get(i), other.get(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    /**
     * Neo:
     * {@return the number of slots in this container}
     */
    public int getSlots() {
        return this.fluids.size();
    }

    /**
     * Neo: Gets a copy of the stack at a particular slot.
     *
     * @param slot The slot to check. Must be within [0, {@link #getSlots()}]
     * @return A copy of the stack in that slot
     * @throws UnsupportedOperationException if the provided slot index is out-of-bounds.
     */
    public FluidStack getStackInSlot(int slot) {
        validateSlotIndex(slot);
        return this.fluids.get(slot).copy();
    }

    /**
     * Neo: Throws {@link UnsupportedOperationException} if the provided slot index is invalid.
     */
    private void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= getSlots()) {
            throw new UnsupportedOperationException("Slot " + slot + " not in valid range - [0," + getSlots() + ")");
        }
    }

    record Slot(int index, FluidStack fluid) {
        public static final Codec<Slot> CODEC = RecordCodecBuilder.create(
            p_331695_ -> p_331695_.group(
                        Codec.intRange(0, 255).fieldOf("slot").forGetter(Slot::index),
                        FluidStack.CODEC.fieldOf("fluid").forGetter(Slot::fluid)
                    )
                    .apply(p_331695_, Slot::new)
        );
    }
}

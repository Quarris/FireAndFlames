package dev.quarris.fireandflames.world.crucible;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CrucibleFluidTank implements IFluidHandler {

    public static final int MAX_FLUID_COUNT = 79;

    private int capacity;
    private int currentStored;
    private List<FluidStack> fluids;

    private Runnable contentsChangedListener;

    public CrucibleFluidTank(int capacity) {
        this.capacity = capacity;
        this.fluids = new ArrayList<>(MAX_FLUID_COUNT);
    }

    public void updateCapacity(int newCapacity) {
        this.capacity = newCapacity;
    }

    public void setListener(Runnable listener) {
        this.contentsChangedListener = listener;
    }

    @Override
    public int fill(FluidStack pResource, @NotNull FluidAction pAction) {
        if (pResource.isEmpty() && this.getRemainingVolume() <= 0) {
            return 0;
        }

        if (pAction.simulate()) {
            return Math.min(pResource.getAmount(), this.getRemainingVolume());
        }

        FluidStack storedFluid = FluidStack.EMPTY;
        for (FluidStack fluid : this.fluids) {
            if (FluidStack.isSameFluidSameComponents(fluid, pResource)) {
                storedFluid = fluid;
            }
        }

        if (storedFluid.isEmpty()) {
            if (this.fluids.size() >= MAX_FLUID_COUNT) {
                return 0;
            }
            storedFluid = pResource.copyWithAmount(Math.min(this.getRemainingVolume(), pResource.getAmount()));
            this.fluids.addLast(storedFluid);
            this.currentStored += storedFluid.getAmount();
            this.onContentsChanged();
            return storedFluid.getAmount();
        }

        int filled = this.getRemainingVolume();

        if (pResource.getAmount() <= filled) {
            storedFluid.grow(pResource.getAmount());
            filled = pResource.getAmount();
        } else {
            storedFluid.grow(filled);
        }

        if (filled > 0) {
            this.onContentsChanged();
            this.currentStored += filled;
        }

        return filled;
    }

    @Override
    public FluidStack drain(FluidStack pResource, @NotNull FluidAction pAction) {
        if (this.fluids.isEmpty()) {
            return FluidStack.EMPTY;
        }

        FluidStack storedFluid = FluidStack.EMPTY;
        for (FluidStack fluid : this.fluids) {
            if (FluidStack.isSameFluidSameComponents(fluid, pResource)) {
                storedFluid = fluid;
            }
        }

        if (storedFluid.isEmpty()) {
            return FluidStack.EMPTY;
        }

        int drained = pResource.getAmount();
        if (storedFluid.getAmount() < drained) {
            drained = storedFluid.getAmount();
        }

        FluidStack drainedFluid = storedFluid.copyWithAmount(drained);
        if (pAction.execute() && drained > 0) {
            storedFluid.shrink(drained);
            this.currentStored -= drained;
            if (storedFluid.isEmpty()) {
                this.fluids.remove(storedFluid);
            }

            this.onContentsChanged();
        }
        return drainedFluid;
    }

    @Override
    public FluidStack drain(int pMaxDrain, @NotNull FluidAction pAction) {
        if (this.fluids.isEmpty()) {
            return FluidStack.EMPTY;
        }

        int drained = pMaxDrain;
        FluidStack fluid = this.fluids.getFirst();
        if (fluid.getAmount() < drained) {
            drained = fluid.getAmount();
        }

        FluidStack stack = fluid.copyWithAmount(drained);
        if (pAction.execute() && drained > 0) {
            fluid.shrink(drained);
            if (fluid.isEmpty()) {
                this.fluids.remove(fluid);
            }
            this.currentStored -= drained;
            this.onContentsChanged();
        }
        return stack;
    }

    public void setFirst(int slot) {
        if (slot < 0 || slot >= this.getTanks()) {
            return;
        }

        FluidStack shifted = this.fluids.remove(slot);
        this.fluids.addFirst(shifted);
        this.onContentsChanged();
    }

    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < this.fluids.size(); i++) {
            if (!this.fluids.get(i).isEmpty()) {
                CompoundTag fluidTag = new CompoundTag();
                fluidTag.putInt("Slot", i);
                nbtTagList.add(this.fluids.get(i).save(provider, fluidTag));
            }
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put("Fluids", nbtTagList);
        nbt.putInt("Size", this.fluids.size());
        nbt.putInt("CurrentTotalAmount", this.currentStored);
        return nbt;
    }

    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        this.currentStored = nbt.getInt("CurrentTotalAmount");
        int size = nbt.getInt("Size");
        this.fluids = new ArrayList<>(NonNullList.withSize(size, FluidStack.EMPTY));
        ListTag tagList = nbt.getList("Fluids", Tag.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag fluidTag = tagList.getCompound(i);
            int slot = fluidTag.getInt("Slot");

            if (slot >= 0 && slot < size) {
                FluidStack.parse(provider, fluidTag).ifPresent(stack -> this.fluids.set(slot, stack));
            }
        }
    }

    @Override
    public boolean isFluidValid(int pTank, @NotNull FluidStack pFluidStack) {
        return FluidStack.isSameFluidSameComponents(this.getFluidInTank(pTank), pFluidStack);
    }

    @Override
    public FluidStack getFluidInTank(int pTank) {
        return this.fluids.get(pTank);
    }

    public float getRelativeFluidAmountUpTo(int tank) {
        float relativeAmount = 0;
        for (int i = 0; i <= tank; i++) {
            relativeAmount += this.getRelativeFluidAmount(i);
        }

        return relativeAmount;
    }

    public float getRelativeFluidAmount(int tank) {
        FluidStack fluid = this.getFluidInTank(tank);
        return fluid.getAmount() / (float) this.getVirtualCapacity();
    }

    @Override
    public int getTankCapacity(int pTank) {
        if (pTank >= this.fluids.size()) return 0;

        return this.getRemainingVolume() + this.fluids.get(pTank).getAmount();
    }

    @Override
    public int getTanks() {
        return this.fluids.size();
    }

    public int getRemainingVolume() {
        return Math.max(0, this.getCapacity() - this.currentStored);
    }

    public int getStored() {
        return this.currentStored;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public int getVirtualCapacity() {
        return Math.max(this.capacity, this.currentStored);
    }

    public void onContentsChanged() {
        if (this.contentsChangedListener != null) {
            this.contentsChangedListener.run();
        }
    }

    @Override
    public String toString() {
        return this.fluids.toString();
    }
}

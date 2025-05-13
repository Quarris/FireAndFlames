package dev.quarris.fireandflames.world.crucible;

import dev.quarris.fireandflames.util.recipe.FluidInput;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CrucibleFluidTank implements IFluidHandler {

    public static final int MAX_FLUID_COUNT = 79;

    private int capacity;
    private int currentStored;
    private List<FluidStack> fluids;

    private Runnable contentsChangedListener;

    public CrucibleFluidTank(int capacity) {
        this.capacity = capacity;
        this.fluids = new ArrayList<>(8);
    }

    public void updateCapacity(int newCapacity) {
        this.capacity = newCapacity;
    }

    public void setListener(Runnable listener) {
        this.contentsChangedListener = listener;
    }

    /**
     * Simulates the removal and insertion of inputs and alloys to see if the tank has enough space for the alloying operation
     * @param pAlloys The resultant alloys to simulate insertion
     * @param pInputs The inputs to simulated draining with
     * @param pInputsToDrain The list of final stacks to drain from this tank. Passed empty to be filled by this method.
     * @return The number of iterations that can occur for alloying. 0 if the recipe is invalid.
     */
    public int canAlloy(List<FluidStack> pAlloys, List<FluidInput> pInputs, List<FluidStack> pInputsToDrain) {
        int amountDrained = 0;
        int stacksRemoved = 0;
        Map<Integer, FluidStack> finalInputsToDrain = new HashMap<>();

        List<FluidStack> contents = this.getFluids();
        for (FluidInput input : pInputs) {
            FluidStack testStack = FluidStack.EMPTY;
            int testIndex = -1;
            for (int i = 0; i < contents.size(); i++) {
                FluidStack content = contents.get(i);
                testIndex = i;
                if (input.matchesWithAmount(content)) {
                    testStack = content;
                    break;
                }
            }

            if (testStack.isEmpty()) {
                return 0;
            }

            var inputStack = testStack;
            int requiredAmount = input.amount().evaluateInt();
            finalInputsToDrain.compute(testIndex, (id, stack) -> {
                if (stack == null) {
                    return inputStack.copyWithAmount(requiredAmount);
                }

                stack.grow(requiredAmount);
                return stack;
            });
            testStack.shrink(requiredAmount);
            amountDrained += requiredAmount;

            if (testStack.isEmpty()) {
                stacksRemoved++;
            }
        }

        int iterations = finalInputsToDrain.entrySet().stream().mapToInt(entry -> this.getFluids().get(entry.getKey()).getAmount() / entry.getValue().getAmount()).min().orElse(0);

        if (iterations == 0) {
            return 0;
        }

        int toFill = pAlloys.stream().mapToInt(FluidStack::getAmount).sum();
        int alloysAdded = pAlloys.size();
        for (FluidStack alloy : pAlloys) {
            for (FluidStack content : contents) {
                if (FluidStack.isSameFluidSameComponents(alloy, content)) {
                    alloysAdded--;
                    break;
                }
            }
        }

        if (this.getTanks() - stacksRemoved + alloysAdded <= MAX_FLUID_COUNT && this.getRemainingVolume() - amountDrained + toFill <= this.getCapacity()) {
            iterations = Math.min(iterations, (int) Math.floor((float) (this.getRemainingVolume() - amountDrained) / toFill));
            pInputsToDrain.addAll(finalInputsToDrain.values());
            return iterations;
        }

        return 0;
    }

    @Override
    public int fill(FluidStack pResource, @NotNull FluidAction pAction) {
        if (this.fluids.size() >= MAX_FLUID_COUNT || pResource.isEmpty() || this.getRemainingVolume() <= 0) {
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

    public List<FluidStack> getFluids() {
        return this.fluids.stream().map(FluidStack::copy).toList();
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

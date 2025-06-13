package dev.quarris.fireandflames.world.inventory.crafting;

import dev.quarris.fireandflames.util.recipe.FluidInput;
import dev.quarris.fireandflames.util.recipe.IItemOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public abstract class BasicCastingRecipe extends CastingRecipe {

    public final IItemOutput result;
    public final FluidInput fluidInput;

    protected BasicCastingRecipe(IItemOutput result, FluidInput fluidInput, Ingredient itemInput, int coolingTime, boolean consumeInput, boolean moveItem) {
        super(itemInput, coolingTime, consumeInput, moveItem);
        this.result = result;
        this.fluidInput = fluidInput;
    }

    @Override
    public int requiredContents(FluidStack stack, Level level) {
        return this.fluidInput.amount().evaluateInt();
    }

    @Override
    public boolean matches(Input input, Level level) {
        return this.fluidInput.test(input.fluid()) && this.itemInput.test(input.item());
    }

    @Override
    public ItemStack assemble(Input input, HolderLookup.Provider registries) {
        return this.result.createItemStack();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result.createItemStack();
    }

    public IItemOutput getOutput() {
        return this.result;
    }

    public FluidInput getFluidInput() {
        return this.fluidInput;
    }



}

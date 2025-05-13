package dev.quarris.fireandflames.world.crucible.crafting;

import dev.quarris.fireandflames.util.recipe.FluidInput;
import dev.quarris.fireandflames.util.recipe.IItemOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public abstract class CastingRecipe implements Recipe<CastingRecipe.Input> {

    public final IItemOutput result;
    public final FluidInput fluidInput;
    public final Ingredient itemInput;
    public final int coolingTime;
    private final boolean consumeInput;
    private final boolean moveItem;

    protected CastingRecipe(IItemOutput result, FluidInput fluidInput, Ingredient itemInput, int coolingTime, boolean consumeInput, boolean moveItem) {
        this.result = result;
        this.fluidInput = fluidInput;
        this.itemInput = itemInput;
        this.coolingTime = coolingTime;
        this.consumeInput = consumeInput;
        this.moveItem = moveItem;
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
    public boolean canCraftInDimensions(int width, int height) {
        return false;
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

    @Override
    public boolean isSpecial() {
        return true;
    }

    public Ingredient getItemInput() {
        return this.itemInput;
    }

    public int getCoolingTime() {
        return this.coolingTime;
    }

    public boolean consumesInput() {
        return this.consumeInput;
    }

    public boolean shouldMoveItem() {
        return this.moveItem;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(this.itemInput);
        return ingredients;
    }

    public record Input(FluidStack fluid, ItemStack item) implements RecipeInput {

        @Override
        public ItemStack getItem(int index) {
            if (index != 0) {
                throw new IllegalArgumentException("No item for index " + index);
            } else {
                return this.item;
            }
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return this.fluid.isEmpty();
        }
    }
}

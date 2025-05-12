package dev.quarris.fireandflames.world.crucible.crafting;

import dev.quarris.fireandflames.setup.RecipeSetup;
import dev.quarris.fireandflames.util.recipe.FluidInput;
import dev.quarris.fireandflames.util.recipe.IFluidOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public record AlloyingRecipe(
    List<FluidInput> ingredients,
    List<IFluidOutput> results,
    int heat
) implements Recipe<AlloyingRecipe.Input> {

    @Override
    public boolean matches(Input recipeInput, Level level) {
        if (recipeInput.heat < this.heat) return false;

        List<FluidStack> inputs = recipeInput.inputs().stream().map(FluidStack::copy).toList();

        for (FluidInput ingredient : this.ingredients) {
            boolean matched = false;
            for (FluidStack input : inputs) {
                if (ingredient.matchesWithAmount(input)) {
                    input.shrink(ingredient.amount());
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(Input input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSetup.ALLOYING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeSetup.ALLOYING_TYPE.get();
    }

    public record Input(List<FluidStack> inputs, int heat) implements RecipeInput {

        @Override
        public ItemStack getItem(int index) {
            throw new UnsupportedOperationException("No item ingredients for Alloying Recipes");
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return this.inputs.isEmpty();
        }
    }
}

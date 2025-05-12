package dev.quarris.fireandflames.data.recipes;

import dev.quarris.fireandflames.util.recipe.FluidInput;
import dev.quarris.fireandflames.util.recipe.IFluidOutput;
import dev.quarris.fireandflames.world.crucible.crafting.AlloyingRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AlloyingRecipeBuilder implements RecipeBuilder {

    private final List<IFluidOutput> results;
    private List<FluidInput> ingredients;
    private int heat = 800;

    public AlloyingRecipeBuilder(List<IFluidOutput> results) {
        this.results = results;
        this.ingredients = new ArrayList<>();
    }


    public static AlloyingRecipeBuilder alloy(IFluidOutput... results) {
        return new AlloyingRecipeBuilder(List.of(results));
    }

    public AlloyingRecipeBuilder requires(FluidInput ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }

    public AlloyingRecipeBuilder heat(int heat) {
        this.heat = heat;
        return this;
    }

    @Override
    public AlloyingRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        return this;
    }

    @Override
    public AlloyingRecipeBuilder group(@Nullable String groupName) {
        return this;
    }

    @Override
    public Item getResult() {
        return Items.AIR;
    }

    @Override
    public void save(RecipeOutput recipeOutput) {
        this.save(recipeOutput, getDefaultRecipeId(this.results.getFirst().createFluid()));
    }

    @Override
    public void save(RecipeOutput recipeOutput, String id) {
        this.save(recipeOutput, ResourceLocation.parse(id));
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        if (this.ingredients.size() < 2) {
            throw new IllegalStateException("Invalid alloying recipe: " + id + ". Alloys require at least 2 inputs.");
        }

        recipeOutput.accept(id, new AlloyingRecipe(this.ingredients, this.results, this.heat), null);
    }

    static ResourceLocation getDefaultRecipeId(FluidStack result) {
        return BuiltInRegistries.FLUID.getKey(result.getFluid());
    }
}

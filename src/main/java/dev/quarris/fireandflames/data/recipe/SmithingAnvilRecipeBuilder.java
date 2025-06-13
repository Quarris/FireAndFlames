package dev.quarris.fireandflames.data.recipe;

import dev.quarris.fireandflames.util.recipe.IItemOutput;
import dev.quarris.fireandflames.world.inventory.crafting.SmithingAnvilRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SmithingAnvilRecipeBuilder implements RecipeBuilder {

    private String group = "";
    private final Ingredient input;
    private final List<IItemOutput> possibleOutputs = new ArrayList<>();


    private SmithingAnvilRecipeBuilder(
        Ingredient input
    ) {
        this.input = input;
    }

    public static SmithingAnvilRecipeBuilder builder(Ingredient input) {
        return new SmithingAnvilRecipeBuilder(input);
    }


    public SmithingAnvilRecipeBuilder creates(ItemStack output) {
        this.possibleOutputs.add(new IItemOutput.Stack(output));
        return this;
    }

    public SmithingAnvilRecipeBuilder creates(IItemOutput output) {
        this.possibleOutputs.add(output);
        return this;
    }

    public SmithingAnvilRecipeBuilder creates(ItemStack... outputs) {
        Arrays.stream(outputs).map(IItemOutput.Stack::new).forEach(this.possibleOutputs::add);
        return this;
    }

    public SmithingAnvilRecipeBuilder creates(IItemOutput... outputs) {
        this.possibleOutputs.addAll(Arrays.asList(outputs));
        return this;
    }

    public SmithingAnvilRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        return this;
    }

    public SmithingAnvilRecipeBuilder group(String groupName) {
        this.group = groupName;
        return this;
    }

    @Override
    public Item getResult() {
        return this.possibleOutputs.get(0).createItemStack().getItem();
    }

    @Override
    public void save(RecipeOutput recipeOutput) {
        this.save(recipeOutput, getDefaultRecipeId(this.input));
    }

    @Override
    public void save(RecipeOutput recipeOutput, String id) {
        this.save(recipeOutput, ResourceLocation.parse(id));
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        SmithingAnvilRecipe recipe = new SmithingAnvilRecipe(this.group, this.input, this.possibleOutputs);
        recipeOutput.accept(id, recipe, null);
    }

    public static ResourceLocation getDefaultRecipeId(Ingredient input) {
        return BuiltInRegistries.ITEM.getKey(input.getItems()[0].getItem()).withPrefix("smithing_anvil/");
    }
}

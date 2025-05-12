package dev.quarris.fireandflames.data.recipes;

import dev.quarris.fireandflames.world.crucible.crafting.CrucibleRecipe;
import dev.quarris.fireandflames.util.recipe.IFluidOutput;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;

public class CrucibleRecipeBuilder implements RecipeBuilder {

    private final Ingredient ingredient;
    private final IFluidOutput result;
    private final int smeltingTime;

    private ItemStack byproduct = ItemStack.EMPTY;
    private String group = "";
    private int heat = 800;

    private CrucibleRecipeBuilder(
        Ingredient ingredient,
        FluidStack result,
        int smeltingTime
    ) {
        this(ingredient, new IFluidOutput.Stack(result), smeltingTime);
    }

    private CrucibleRecipeBuilder(
        Ingredient ingredient,
        TagKey<Fluid> result,
        int resultAmount,
        int smeltingTime
    ) {
        this(ingredient, new IFluidOutput.Tag(result, resultAmount), smeltingTime);
    }

    private CrucibleRecipeBuilder(
        Ingredient ingredient,
        IFluidOutput result,
        int smeltingTime
    ) {
        this.ingredient = ingredient;
        this.result = result;
        this.smeltingTime = smeltingTime;
    }

    public static CrucibleRecipeBuilder smelting(FluidStack result, Ingredient ingredient, int smeltingTime) {
        return new CrucibleRecipeBuilder(ingredient, result, smeltingTime);
    }

    public static CrucibleRecipeBuilder smelting(TagKey<Fluid> result, int amount, Ingredient ingredient, int smeltingTime) {
        return new CrucibleRecipeBuilder(ingredient, result, amount, smeltingTime);
    }

    public CrucibleRecipeBuilder byproduct(ItemStack byproduct) {
        this.byproduct = byproduct;
        return this;
    }

    public CrucibleRecipeBuilder heat(int heat) {
        this.heat = heat;
        return this;
    }

    public CrucibleRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        return this;
    }

    public CrucibleRecipeBuilder group(String groupName) {
        this.group = groupName;
        return this;
    }

    public FluidStack getFluidResult() {
        return this.result.createFluid();
    }

    @Override
    public Item getResult() {
        return FluidUtil.getFilledBucket(this.getFluidResult()).getItem();
    }

    @Override
    public void save(RecipeOutput recipeOutput) {
        this.save(recipeOutput, getDefaultRecipeId(this.result));
    }

    @Override
    public void save(RecipeOutput recipeOutput, String id) {
        this.save(recipeOutput, ResourceLocation.parse(id));
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        CrucibleRecipe recipe = new CrucibleRecipe(this.group, this.ingredient, this.byproduct, this.result, this.smeltingTime, this.heat);
        recipeOutput.accept(id, recipe, null);
    }

    public static ResourceLocation getDefaultRecipeId(IFluidOutput result) {
        ResourceLocation baseName;
        if (result instanceof IFluidOutput.Tag tag) {
            baseName = tag.tag().location();
        } else if (result instanceof IFluidOutput.Stack stack) {
            baseName = BuiltInRegistries.FLUID.getKey(stack.createFluid().getFluid());
        } else {
            throw new UnsupportedOperationException("Invalid fluid recipe output");
        }

        return baseName.withPrefix("crucible/");
    }
}

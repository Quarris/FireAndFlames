package dev.quarris.fireandflames.data.recipes;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.world.crucible.crafting.CrucibleRecipe;
import dev.quarris.fireandflames.world.crucible.crafting.IFluidRecipeOutput;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class CrucibleRecipeBuilder implements RecipeBuilder {

    private final RecipeCategory category;
    private final CookingBookCategory bookCategory;
    private final Ingredient ingredient;
    private final IFluidRecipeOutput result;
    private final int smeltingTime;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    private ItemStack byproduct = ItemStack.EMPTY;
    private String group;

    private CrucibleRecipeBuilder(
        RecipeCategory category,
        CookingBookCategory bookCategory,
        Ingredient ingredient,
        FluidStack result,
        int smeltingTime
    ) {
        this(category, bookCategory, ingredient, new IFluidRecipeOutput.Direct(result), smeltingTime);
    }

    private CrucibleRecipeBuilder(
        RecipeCategory category,
        CookingBookCategory bookCategory,
        Ingredient ingredient,
        TagKey<Fluid> result,
        int resultAmount,
        int smeltingTime
    ) {
        this(category, bookCategory, ingredient, new IFluidRecipeOutput.Tag(result, resultAmount), smeltingTime);
    }

    private CrucibleRecipeBuilder(
        RecipeCategory category,
        CookingBookCategory bookCategory,
        Ingredient ingredient,
        IFluidRecipeOutput result,
        int smeltingTime
    ) {
        this.category = category;
        this.bookCategory = bookCategory;
        this.ingredient = ingredient;
        this.result = result;
        this.smeltingTime = smeltingTime;
    }

    public static CrucibleRecipeBuilder smelting(RecipeCategory category, FluidStack result, Ingredient ingredient, int smeltingTime) {
        return new CrucibleRecipeBuilder(category, CookingBookCategory.BLOCKS, ingredient, result, smeltingTime);
    }

    public static CrucibleRecipeBuilder smelting(RecipeCategory category, TagKey<Fluid> result, int amount, Ingredient ingredient, int smeltingTime) {
        return new CrucibleRecipeBuilder(category, CookingBookCategory.BLOCKS, ingredient, result, amount, smeltingTime);
    }

    public CrucibleRecipeBuilder byproduct(ItemStack byproduct) {
        this.byproduct = byproduct;
        return this;
    }

    public CrucibleRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
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
        this.ensureValid(id);
        Advancement.Builder advancement$builder = recipeOutput.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement$builder::addCriterion);
        CrucibleRecipe recipe = new CrucibleRecipe(this.group, this.bookCategory, this.ingredient, this.byproduct, this.result, this.smeltingTime);
        recipeOutput.accept(id, recipe, advancement$builder.build(id.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    private static CookingBookCategory determineSmeltingRecipeCategory(ItemLike result) {
        if (result.asItem().components().has(DataComponents.FOOD)) {
            return CookingBookCategory.FOOD;
        } else {
            return result.asItem() instanceof BlockItem ? CookingBookCategory.BLOCKS : CookingBookCategory.MISC;
        }
    }

    private static CookingBookCategory determineBlastingRecipeCategory(ItemLike result) {
        return result.asItem() instanceof BlockItem ? CookingBookCategory.BLOCKS : CookingBookCategory.MISC;
    }

    private static CookingBookCategory determineRecipeCategory(RecipeSerializer<? extends AbstractCookingRecipe> serializer, ItemLike result) {
        if (serializer == RecipeSerializer.SMELTING_RECIPE) {
            return determineSmeltingRecipeCategory(result);
        } else if (serializer == RecipeSerializer.BLASTING_RECIPE) {
            return determineBlastingRecipeCategory(result);
        } else if (serializer != RecipeSerializer.SMOKING_RECIPE && serializer != RecipeSerializer.CAMPFIRE_COOKING_RECIPE) {
            throw new IllegalStateException("Unknown cooking recipe type");
        } else {
            return CookingBookCategory.FOOD;
        }
    }

    /**
     * Makes sure that this obtainable.
     */
    private void ensureValid(ResourceLocation id) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + id);
        }
    }

    public static ResourceLocation getDefaultRecipeId(IFluidRecipeOutput result) {
        ResourceLocation baseName;
        if (result instanceof IFluidRecipeOutput.Tag tag) {
            baseName = tag.tag().location();
        } else if (result instanceof IFluidRecipeOutput.Direct direct) {
            baseName = BuiltInRegistries.FLUID.getKey(direct.createFluid().getFluid());
        } else {
            throw new UnsupportedOperationException("Invalid fluid recipe output");
        }

        return baseName.withPrefix("crucible/");
    }
}

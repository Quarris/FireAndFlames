package dev.quarris.fireandflames.datagen.server.recipe;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.config.number.INumberProvider;
import dev.quarris.fireandflames.util.recipe.IItemOutput;
import dev.quarris.fireandflames.util.recipe.ItemInput;
import dev.quarris.fireandflames.world.inventory.crafting.ArtisanCraftingRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public class ArtisanCraftingRecipeBuilder implements RecipeBuilder {

    private final IItemOutput result;
    private final ItemInput ingredient;

    private String group = "";
    private IItemOutput byproduct = new IItemOutput.Stack(ItemStack.EMPTY);

    public static ArtisanCraftingRecipeBuilder builder(IItemOutput result, ItemInput ingredient) {
        return new ArtisanCraftingRecipeBuilder(result, ingredient);
    }

    public ArtisanCraftingRecipeBuilder(IItemOutput result, ItemInput ingredient) {
        this.ingredient = ingredient;
        this.result = result;
    }

    public ArtisanCraftingRecipeBuilder byproduct(IItemOutput byproduct) {
        this.byproduct = byproduct;
        return this;
    }

    public ArtisanCraftingRecipeBuilder byproduct(ItemStack byproduct) {
        this.byproduct = new IItemOutput.Stack(byproduct);
        return this;
    }

    public ArtisanCraftingRecipeBuilder byproduct(ItemLike byproduct) {
        this.byproduct = new IItemOutput.Stack(byproduct);
        return this;
    }

    public ArtisanCraftingRecipeBuilder byproduct(ItemLike byproduct, INumberProvider count) {
        this.byproduct = new IItemOutput.Stack(byproduct, count);
        return this;
    }

    public ArtisanCraftingRecipeBuilder byproduct(ItemLike byproduct, int count) {
        this.byproduct = new IItemOutput.Stack(byproduct, count);
        return this;
    }

    public ArtisanCraftingRecipeBuilder byproduct(TagKey<Item> byproduct, INumberProvider count) {
        this.byproduct = new IItemOutput.Tag(byproduct);
        return this;
    }

    @Override
    public ArtisanCraftingRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        return this;
    }

    @Override
    public ArtisanCraftingRecipeBuilder group(@Nullable String groupName) {
        this.group = groupName;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result.createItemStack().getItem();
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
        ArtisanCraftingRecipe recipe = new ArtisanCraftingRecipe(this.group, this.ingredient, this.result, this.byproduct);
        recipeOutput.accept(id, recipe, null);
    }

    public static ResourceLocation getDefaultRecipeId(IItemOutput input) {
        ResourceLocation defaultId = BuiltInRegistries.ITEM.getKey(input.createItemStack().getItem()).withPrefix("artisan_crafting/");
        return ModRef.res(defaultId.getPath());
    }
}

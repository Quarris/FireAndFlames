package dev.quarris.fireandflames.data.recipe;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.config.number.ConstantNumber;
import dev.quarris.fireandflames.data.config.number.INumberProvider;
import dev.quarris.fireandflames.util.recipe.IItemOutput;
import dev.quarris.fireandflames.util.recipe.ItemInput;
import dev.quarris.fireandflames.world.inventory.crafting.ArtisanCraftingRecipe;
import dev.quarris.fireandflames.world.inventory.crafting.MaterialArtisanCraftingRecipe;
import dev.quarris.fireandflames.world.inventory.crafting.MaterialCastingRecipe;
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

public class MaterialArtisanCraftingRecipeBuilder implements RecipeBuilder {

    private final INumberProvider units;
    private final IItemOutput result;

    public static MaterialArtisanCraftingRecipeBuilder builder(IItemOutput result, INumberProvider units) {
        return new MaterialArtisanCraftingRecipeBuilder(result, units);
    }

    public static MaterialArtisanCraftingRecipeBuilder builder(IItemOutput result, int units) {
        return new MaterialArtisanCraftingRecipeBuilder(result, new ConstantNumber(units));
    }

    public static MaterialArtisanCraftingRecipeBuilder builder(ItemStack result, INumberProvider units) {
        return new MaterialArtisanCraftingRecipeBuilder(new IItemOutput.Stack(result), units);
    }

    public static MaterialArtisanCraftingRecipeBuilder builder(ItemLike result, INumberProvider units) {
        return new MaterialArtisanCraftingRecipeBuilder(new IItemOutput.Stack(result), units);
    }

    public static MaterialArtisanCraftingRecipeBuilder builder(ItemLike result, int units) {
        return new MaterialArtisanCraftingRecipeBuilder(new IItemOutput.Stack(result), new ConstantNumber(units));
    }

    public static MaterialArtisanCraftingRecipeBuilder builder(TagKey<Item> itemTag, INumberProvider count, INumberProvider units) {
        return new MaterialArtisanCraftingRecipeBuilder(new IItemOutput.Tag(itemTag, count), units);
    }

    public static MaterialArtisanCraftingRecipeBuilder builder(TagKey<Item> itemTag, INumberProvider count, int units) {
        return new MaterialArtisanCraftingRecipeBuilder(new IItemOutput.Tag(itemTag, count), new ConstantNumber(units));
    }

    public static MaterialArtisanCraftingRecipeBuilder builder(TagKey<Item> itemTag, int count, INumberProvider units) {
        return new MaterialArtisanCraftingRecipeBuilder(new IItemOutput.Tag(itemTag, count), units);
    }

    public static MaterialArtisanCraftingRecipeBuilder builder(TagKey<Item> itemTag, int count, int units) {
        return new MaterialArtisanCraftingRecipeBuilder(new IItemOutput.Tag(itemTag, count), new ConstantNumber(units));
    }
    private MaterialArtisanCraftingRecipeBuilder(IItemOutput result, INumberProvider units) {
        this.units = units;
        this.result = result;
    }

    @Override
    public MaterialArtisanCraftingRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        return this;
    }

    @Override
    public MaterialArtisanCraftingRecipeBuilder group(@Nullable String groupName) {
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
        MaterialArtisanCraftingRecipe recipe = new MaterialArtisanCraftingRecipe(this.units, this.result);
        recipeOutput.accept(id, recipe, null);
    }

    public static ResourceLocation getDefaultRecipeId(IItemOutput input) {
        ResourceLocation defaultId = BuiltInRegistries.ITEM.getKey(input.createItemStack().getItem()).withPrefix("artisan_crafting/");
        return ModRef.res(defaultId.getPath());
    }
}

package dev.quarris.fireandflames.data.recipe;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.config.number.ConstantNumber;
import dev.quarris.fireandflames.data.config.number.INumberProvider;
import dev.quarris.fireandflames.util.recipe.IItemOutput;
import dev.quarris.fireandflames.util.recipe.ItemInput;
import dev.quarris.fireandflames.world.inventory.crafting.FamilyArtisanCraftingRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class FamilyArtisanCraftingRecipeBuilder implements RecipeBuilder {

    private final Ingredient ingredient;
    private final Map<BlockFamily.Variant, FamilyArtisanCraftingRecipe.FamilyVariantOutput> variants = new HashMap<>();

    private String group = "";

    public static FamilyArtisanCraftingRecipeBuilder builder(Ingredient ingredient) {
        return new FamilyArtisanCraftingRecipeBuilder(ingredient);
    }

    public FamilyArtisanCraftingRecipeBuilder(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public FamilyArtisanCraftingRecipeBuilder variant(BlockFamily.Variant variant, INumberProvider requires, INumberProvider outputs, IItemOutput byproduct) {
        this.variant(variant, new FamilyArtisanCraftingRecipe.FamilyVariantOutput(requires, outputs, byproduct));
        return this;
    }
    public FamilyArtisanCraftingRecipeBuilder variant(BlockFamily.Variant variant, INumberProvider requires, INumberProvider outputs) {
        this.variant(variant, new FamilyArtisanCraftingRecipe.FamilyVariantOutput(requires, outputs, new IItemOutput.Stack(ItemStack.EMPTY)));
        return this;
    }

    public FamilyArtisanCraftingRecipeBuilder variant(BlockFamily.Variant variant) {
        this.variant(variant, new ConstantNumber(1), new ConstantNumber(1), new IItemOutput.Stack(ItemStack.EMPTY));
        return this;
    }

    public FamilyArtisanCraftingRecipeBuilder variant(BlockFamily.Variant variant, FamilyArtisanCraftingRecipe.FamilyVariantOutput output) {
        this.variants.put(variant, output);
        return this;
    }


    @Override
    public FamilyArtisanCraftingRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        return this;
    }

    @Override
    public FamilyArtisanCraftingRecipeBuilder group(@Nullable String groupName) {
        this.group = groupName;
        return this;
    }

    @Override
    public Item getResult() {
        return BlockFamilies.getAllFamilies().map(family -> new ItemStack(family.getBaseBlock())).filter(this.ingredient::test).findFirst().orElse(ItemStack.EMPTY).getItem();
    }

    @Override
    public void save(RecipeOutput recipeOutput) {
        this.save(recipeOutput, getDefaultRecipeId(this.getResult()));
    }

    @Override
    public void save(RecipeOutput recipeOutput, String id) {
        this.save(recipeOutput, id.contains(":") ? ResourceLocation.parse(id) : ModRef.res(id));
    }

    public void saveAtDefaultPath(RecipeOutput recipeOutput, String id) {
        this.saveAtDefaultPath(recipeOutput, id.contains(":") ? ResourceLocation.parse(id) : ModRef.res(id));
    }

    public void saveAtDefaultPath(RecipeOutput recipeOutput, ResourceLocation id) {
        this.save(recipeOutput, id.withPrefix("artisan_crafting/"));
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        FamilyArtisanCraftingRecipe recipe = new FamilyArtisanCraftingRecipe(this.group, this.ingredient, this.variants);
        recipeOutput.accept(id, recipe, null);
    }

    public static ResourceLocation getDefaultRecipeId(Item input) {
        ResourceLocation defaultId = BuiltInRegistries.ITEM.getKey(input).withPrefix("artisan_crafting/");
        return ModRef.res(defaultId.getPath());
    }
}

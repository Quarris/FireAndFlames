package dev.quarris.fireandflames.data.recipes;

import dev.quarris.fireandflames.setup.RecipeSetup;
import dev.quarris.fireandflames.world.crucible.crafting.BasinCastingRecipe;
import dev.quarris.fireandflames.world.crucible.crafting.CastingRecipe;
import dev.quarris.fireandflames.world.crucible.crafting.TableCastingRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.jetbrains.annotations.Nullable;

public class CastingRecipeBuilder implements RecipeBuilder {

    private final RecipeType<?> type;
    private final FluidIngredient fluidInput;
    private final ItemStack result;

    private int coolingTime = 100;
    private Ingredient itemInput = Ingredient.EMPTY;
    private boolean consumesItem;

    private CastingRecipeBuilder(RecipeType<?> type, FluidIngredient fluidInput, ItemStack result) {
        this.type = type;
        this.fluidInput = fluidInput;
        this.result = result;
    }



    public static CastingRecipeBuilder basin(FluidIngredient fluid, ItemStack result) {
        return new CastingRecipeBuilder(RecipeSetup.BASIN_CASTING_TYPE.get(), fluid, result).consumesItem(true);
    }

    public static CastingRecipeBuilder table(FluidIngredient fluid, ItemStack result) {
        return new CastingRecipeBuilder(RecipeSetup.TABLE_CASTING_TYPE.get(), fluid, result);
    }

    public CastingRecipeBuilder coolingTime(int time) {
        this.coolingTime = time;
        return this;
    }

    public CastingRecipeBuilder withItemInput(Ingredient input) {
        this.itemInput = input;
        return this;
    }

    public CastingRecipeBuilder consumesItem(boolean consumesItem) {
        this.consumesItem = consumesItem;
        return this;
    }

    @Override
    public CastingRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        return this;
    }

    @Override
    public CastingRecipeBuilder group(@Nullable String groupName) {
        return this;
    }

    @Override
    public Item getResult() {
        return this.result.getItem();
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
        CastingRecipe recipe = null;
        if (this.type == RecipeSetup.BASIN_CASTING_TYPE.get()) {
            recipe = new BasinCastingRecipe(this.result, this.fluidInput, this.itemInput, this.coolingTime, this.consumesItem);
        }

        if (this.type == RecipeSetup.TABLE_CASTING_TYPE.get()) {
            recipe = new TableCastingRecipe(this.result, this.fluidInput, this.itemInput, this.coolingTime, this.consumesItem);
        }

        if (recipe != null) {
            recipeOutput.accept(id, recipe, null);
        }
    }

    static ResourceLocation getDefaultRecipeId(ItemStack itemLike) {
        return BuiltInRegistries.ITEM.getKey(itemLike.getItem());
    }
}

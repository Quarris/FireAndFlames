package dev.quarris.fireandflames.data.recipes;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.config.number.INumberProvider;
import dev.quarris.fireandflames.setup.RecipeSetup;
import dev.quarris.fireandflames.util.recipe.FluidInput;
import dev.quarris.fireandflames.util.recipe.IItemOutput;
import dev.quarris.fireandflames.world.crucible.crafting.BasinCastingRecipe;
import dev.quarris.fireandflames.world.crucible.crafting.CastingRecipe;
import dev.quarris.fireandflames.world.crucible.crafting.TableCastingRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.jetbrains.annotations.Nullable;

public class CastingRecipeBuilder implements RecipeBuilder {

    private final RecipeType<?> type;
    private final FluidInput fluidInput;
    private final IItemOutput result;

    private int coolingTime = 100;
    private Ingredient itemInput = Ingredient.EMPTY;
    private boolean consumesItem;

    private CastingRecipeBuilder(RecipeType<?> type, FluidInput fluidInput, IItemOutput result) {
        this.type = type;
        this.fluidInput = fluidInput;
        this.result = result;
    }

    public static CastingRecipeBuilder basin(FluidIngredient fluid, int amount, IItemOutput result) {
        return new CastingRecipeBuilder(RecipeSetup.BASIN_CASTING_TYPE.get(), new FluidInput(fluid, amount), result).consumesItem(true);
    }

    public static CastingRecipeBuilder basin(FluidIngredient fluid, INumberProvider amount, IItemOutput result) {
        return new CastingRecipeBuilder(RecipeSetup.BASIN_CASTING_TYPE.get(), new FluidInput(fluid, amount), result).consumesItem(true);
    }

    public static CastingRecipeBuilder basin(FluidStack fluid, IItemOutput result) {
        return new CastingRecipeBuilder(RecipeSetup.BASIN_CASTING_TYPE.get(), new FluidInput(FluidIngredient.single(fluid), fluid.getAmount()), result).consumesItem(true);
    }

    public static CastingRecipeBuilder table(FluidIngredient fluid, int amount, IItemOutput result) {
        return new CastingRecipeBuilder(RecipeSetup.TABLE_CASTING_TYPE.get(), new FluidInput(fluid, amount), result);
    }

    public static CastingRecipeBuilder table(FluidIngredient fluid, INumberProvider amount, IItemOutput result) {
        return new CastingRecipeBuilder(RecipeSetup.TABLE_CASTING_TYPE.get(), new FluidInput(fluid, amount), result);
    }

    public static CastingRecipeBuilder table(FluidStack fluid, IItemOutput result) {
        return new CastingRecipeBuilder(RecipeSetup.TABLE_CASTING_TYPE.get(), new FluidInput(FluidIngredient.single(fluid), fluid.getAmount()), result);
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

    public void saveFnf(RecipeOutput pOutput) {
        this.save(pOutput, this.getModdedRecipeId(this.result));
    }

    private ResourceLocation getModdedRecipeId(IItemOutput item) {
        return ModRef.res(getDefaultRecipeId(item).getPath()).withPrefix("casting/" + (this.type == RecipeSetup.BASIN_CASTING_TYPE.get() ? "basin/" : "table/"));
    }

    static ResourceLocation getDefaultRecipeId(IItemOutput output) {
        if (output instanceof IItemOutput.Stack(ItemStack stack)) {
            return BuiltInRegistries.ITEM.getKey(stack.getItem());
        } else if (output instanceof IItemOutput.Tag tagOutput) {
            return tagOutput.tag().location();
        }

        throw new IllegalArgumentException("Invalid IItemOutput type. Not Stack nor Tag");
    }
}

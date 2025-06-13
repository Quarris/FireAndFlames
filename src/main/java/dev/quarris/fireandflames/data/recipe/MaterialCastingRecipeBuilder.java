package dev.quarris.fireandflames.data.recipe;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.config.number.ConstantNumber;
import dev.quarris.fireandflames.data.config.number.INumberProvider;
import dev.quarris.fireandflames.setup.RecipeSetup;
import dev.quarris.fireandflames.util.recipe.IItemOutput;
import dev.quarris.fireandflames.world.inventory.crafting.CastingRecipe;
import dev.quarris.fireandflames.world.inventory.crafting.TableMaterialCastingRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public class MaterialCastingRecipeBuilder implements RecipeBuilder {

    private final RecipeType<?> type;
    private final INumberProvider fluidInput;
    private final IItemOutput result;

    private int coolingTime = 100;
    private Ingredient itemInput = Ingredient.EMPTY;
    private boolean consumesInput;
    private boolean moveItem;

    private MaterialCastingRecipeBuilder(RecipeType<?> type, INumberProvider fluidInput, IItemOutput result) {
        this.type = type;
        this.fluidInput = fluidInput;
        this.result = result;
    }

    public static MaterialCastingRecipeBuilder basin(int amount, IItemOutput result) {
        return new MaterialCastingRecipeBuilder(RecipeSetup.BASIN_CASTING_TYPE.get(), new ConstantNumber(amount), result).consumesInput(true);
    }

    public static MaterialCastingRecipeBuilder basin(INumberProvider amount, IItemOutput result) {
        return new MaterialCastingRecipeBuilder(RecipeSetup.BASIN_CASTING_TYPE.get(), amount, result).consumesInput(true);
    }

    public static MaterialCastingRecipeBuilder table(int amount, IItemOutput result) {
        return new MaterialCastingRecipeBuilder(RecipeSetup.TABLE_MATERIAL_CASTING_TYPE.get(), new ConstantNumber(amount), result);
    }

    public static MaterialCastingRecipeBuilder table(INumberProvider amount, IItemOutput result) {
        return new MaterialCastingRecipeBuilder(RecipeSetup.TABLE_MATERIAL_CASTING_TYPE.get(), amount, result);
    }

    public MaterialCastingRecipeBuilder coolingTime(int time) {
        this.coolingTime = time;
        return this;
    }

    public MaterialCastingRecipeBuilder withItemInput(Ingredient input) {
        this.itemInput = input;
        return this;
    }

    public MaterialCastingRecipeBuilder withItemInput(ItemLike input) {
        this.itemInput = Ingredient.of(input);
        return this;
    }

    public MaterialCastingRecipeBuilder consumesInput(boolean consumesInput) {
        this.consumesInput = consumesInput;
        return this;
    }

    public MaterialCastingRecipeBuilder moveItem(boolean moveItem) {
        this.moveItem = moveItem;
        return this;
    }

    @Override
    public MaterialCastingRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        return this;
    }

    @Override
    public MaterialCastingRecipeBuilder group(@Nullable String groupName) {
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

        if (this.type == RecipeSetup.TABLE_MATERIAL_CASTING_TYPE.get()) {
            recipe = new TableMaterialCastingRecipe(this.result, this.fluidInput, this.itemInput, this.coolingTime, this.consumesInput, this.moveItem);
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
        if (output instanceof IItemOutput.Stack(ItemStack stack, INumberProvider count)) {
            return BuiltInRegistries.ITEM.getKey(stack.getItem());
        } else if (output instanceof IItemOutput.Tag tagOutput) {
            return tagOutput.tag().location();
        }

        throw new IllegalArgumentException("Invalid IItemOutput type. Not Stack nor Tag");
    }
}

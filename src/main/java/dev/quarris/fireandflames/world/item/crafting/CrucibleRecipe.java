package dev.quarris.fireandflames.world.item.crafting;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import dev.quarris.fireandflames.setup.BlockSetup;
import dev.quarris.fireandflames.setup.RecipeSetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.Function;

public class CrucibleRecipe implements Recipe<SingleRecipeInput> {

    public static final Codec<Either<FluidStack, Pair<TagKey<Fluid>, Integer>>> RESULT_CODEC = Codec.either(
        FluidStack.CODEC,
        Codec.pair(
            TagKey.codec(Registries.FLUID).fieldOf("tag").codec(),
            Codec.INT.fieldOf("amount").codec()
        )
    );

    protected final RecipeType<?> type;
    protected final String group;
    protected final CookingBookCategory category;
    protected final Ingredient ingredient;
    protected final ItemStack byproduct;
    protected final Either<FluidStack, Pair<TagKey<Fluid>, Integer>> eitherResult;
    protected final int smeltingTime;

    public CrucibleRecipe(String group, CookingBookCategory category, Ingredient ingredient, ItemStack byproduct, Either<FluidStack, Pair<TagKey<Fluid>, Integer>> eitherResult, int smeltingTime) {
        this(RecipeSetup.CRUCIBLE_TYPE.get(), group, category, ingredient, byproduct, eitherResult, smeltingTime);
    }

    public CrucibleRecipe(RecipeType<?> type, String group, CookingBookCategory category, Ingredient ingredient, ItemStack byproduct, TagKey<Fluid> resultTag, int resultAmount, int smeltingTime) {
        this(RecipeSetup.CRUCIBLE_TYPE.get(), group, category, ingredient, byproduct, Either.right(Pair.of(resultTag, resultAmount)), smeltingTime);
    }

    public CrucibleRecipe(String group, CookingBookCategory category, Ingredient ingredient, ItemStack byproduct, FluidStack result, int smeltingTime) {
        this(RecipeSetup.CRUCIBLE_TYPE.get(), group, category, ingredient, byproduct, Either.left(result), smeltingTime);
    }

    protected CrucibleRecipe(RecipeType<?> type, String group, CookingBookCategory category, Ingredient ingredient, ItemStack byproduct, Either<FluidStack, Pair<TagKey<Fluid>, Integer>> eitherResult, int smeltingTime) {
        this.type = type;
        this.category = category;
        this.group = group;
        this.ingredient = ingredient;
        this.byproduct = byproduct;
        this.eitherResult = eitherResult;
        this.smeltingTime = smeltingTime;
    }

    public FluidStack getFluidResult() {
        return this.eitherResult.map(Function.identity(), pair -> ofTag(pair.getFirst(), pair.getSecond()));
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return this.ingredient.test(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        ItemStack copy = this.byproduct.copy();
        copy.setCount(1);
        return copy;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(this.ingredient);
        return ingredients;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.byproduct;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(BlockSetup.CRUCIBLE_CONTROLLER.get());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSetup.CRUCIBLE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeSetup.CRUCIBLE_TYPE.get();
    }

    public static FluidStack ofTag(TagKey<Fluid> tag, int amount) {
        // TODO maybe add config that targets default stack from a tag instead of getting the first entry
        return new FluidStack(BuiltInRegistries.FLUID.getTag(tag).get().get(0), amount);
    }
}

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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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

    public FluidStack getFluidResult() {
        return this.eitherResult.map(Function.identity(), pair -> ofTag(pair.getFirst(), pair.getSecond()));
    }

    public ItemStack getByproduct() {
        return this.byproduct;
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
        return BuiltInRegistries.FLUID.getTag(tag).map(tags -> new FluidStack(tags.get(0), amount)).orElse(FluidStack.EMPTY);
    }

    public static class Active {
        private ResourceLocation lastRecipeId;
        private RecipeHolder<CrucibleRecipe> recipe;
        private int ticks;

        public boolean hasRecipe() {
            return this.recipe != null;
        }

        public void updateWith(Level level, ItemStack input) {
            if (this.hasRecipe()) {
                // Check current recipe is its still valid
                if (this.recipe.value().matches(new SingleRecipeInput(input), level)) {
                    if (this.isFinished()) {
                        // The recipe is still valid but has finished, no need to update.
                        return;
                    }

                    // Progress the recipe.
                    if (this.isActive()) {
                        this.ticks++;
                    }
                    return;
                }

                // Input no longer matches the current recipe
                this.reset();
            }

            if (!this.hasRecipe()) {
                // Set recipe based on input
                this.recipe = level.getRecipeManager().getRecipeFor(RecipeSetup.CRUCIBLE_TYPE.get(), new SingleRecipeInput(input), level, this.lastRecipeId).orElse(null);
            }
        }

        public void reset() {
            this.lastRecipeId = this.recipe.id();
            this.ticks = 0;
            this.recipe = null;
        }

        public double getProgress() {
            if (!this.hasRecipe()) return 0;
            return this.ticks / (double) this.recipe.value().smeltingTime;
        }

        public boolean isFinished() {
            if (!this.hasRecipe()) return false;
            return this.ticks == this.recipe.value().smeltingTime;
        }

        public boolean isActive() {
            if (!this.hasRecipe()) return false;
            return this.ticks < this.recipe.value().smeltingTime;
        }

        public FluidStack createOutput() {
            return this.recipe.value().getFluidResult().copy();
        }

        public ItemStack createByproduct() {
            return this.recipe.value().getByproduct().copy();
        }

        public int getTicks() {
            return this.ticks;
        }

        public void setTicks(int ticks) {
            this.ticks = ticks;
        }

        @Override
        public String toString() {
            if (!this.hasRecipe()) return "<x>";
            return this.recipe.id() + " @ " + this.ticks + "/" + this.recipe.value().smeltingTime;
        }

        public void serializeNbt(CompoundTag pTag, HolderLookup.Provider pRegistries) {
            pTag.putInt("Ticks", this.ticks);
            pTag.putString("RecipeId", this.recipe.id().toString());
        }

        public void deserializeNbt(CompoundTag pTag, HolderLookup.Provider pRegistries) {
            this.ticks = pTag.getInt("Ticks");
            this.recipe = pRegistries.asGetterLookup().get(Registries.RECIPE, ResourceKey.create(Registries.RECIPE, ResourceLocation.parse(pTag.getString("RecipeId")))).map(ref -> new RecipeHolder<>(ref.key().location(), (CrucibleRecipe) ref.value())).orElse(null);
        }
    }
}

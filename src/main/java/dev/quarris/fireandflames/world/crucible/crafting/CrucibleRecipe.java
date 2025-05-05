package dev.quarris.fireandflames.world.crucible.crafting;

import dev.quarris.fireandflames.setup.BlockSetup;
import dev.quarris.fireandflames.setup.RecipeSetup;
import dev.quarris.fireandflames.util.recipe.IFluidOutput;
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

public record CrucibleRecipe(
    RecipeType<?> type,
    String group,
    Ingredient ingredient,
    ItemStack byproduct,
    IFluidOutput result,
    int smeltingTime
) implements Recipe<SingleRecipeInput> {

    public CrucibleRecipe(String group, Ingredient ingredient, ItemStack byproduct, IFluidOutput eitherResult, int smeltingTime) {
        this(RecipeSetup.CRUCIBLE_TYPE.get(), group, ingredient, byproduct, eitherResult, smeltingTime);
    }

    public CrucibleRecipe(RecipeType<?> type, String group, Ingredient ingredient, ItemStack byproduct, TagKey<Fluid> resultTag, int resultAmount, int smeltingTime) {
        this(RecipeSetup.CRUCIBLE_TYPE.get(), group, ingredient, byproduct, new IFluidOutput.Tag(resultTag, resultAmount), smeltingTime);
    }

    public CrucibleRecipe(String group, Ingredient ingredient, ItemStack byproduct, FluidStack result, int smeltingTime) {
        this(RecipeSetup.CRUCIBLE_TYPE.get(), group, ingredient, byproduct, new IFluidOutput.Stack(result), smeltingTime);
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
    public boolean isSpecial() {
        return true;
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
        return this.result.createFluid();
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

        public float getProgress() {
            if (!this.hasRecipe()) return 0;
            return this.ticks / (float) this.recipe.value().smeltingTime;
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
            return this.recipe.value().byproduct().copy();
        }

        public int getTicks() {
            if (!this.hasRecipe()) return 0;
            return (int) Math.ceil((this.ticks / (double) this.recipe.value().smeltingTime) * 100);
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

package dev.quarris.fireandflames.world.crucible.crafting;

import dev.quarris.fireandflames.config.ServerConfigs;
import dev.quarris.fireandflames.setup.BlockSetup;
import dev.quarris.fireandflames.setup.RecipeSetup;
import dev.quarris.fireandflames.util.recipe.IFluidOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
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
    int smeltingTime,
    int heat
) implements Recipe<CrucibleRecipe.Input> {

    public CrucibleRecipe(String group, Ingredient ingredient, ItemStack byproduct, IFluidOutput eitherResult, int smeltingTime, int heat) {
        this(RecipeSetup.CRUCIBLE_TYPE.get(), group, ingredient, byproduct, eitherResult, smeltingTime, heat);
    }

    public CrucibleRecipe(RecipeType<?> type, String group, Ingredient ingredient, ItemStack byproduct, TagKey<Fluid> resultTag, int resultAmount, int smeltingTime, int heat) {
        this(RecipeSetup.CRUCIBLE_TYPE.get(), group, ingredient, byproduct, new IFluidOutput.Tag(resultTag, resultAmount), smeltingTime, heat);
    }

    public CrucibleRecipe(String group, Ingredient ingredient, ItemStack byproduct, FluidStack result, int smeltingTime, int heat) {
        this(RecipeSetup.CRUCIBLE_TYPE.get(), group, ingredient, byproduct, new IFluidOutput.Stack(result), smeltingTime, heat);
    }

    @Override
    public boolean matches(Input input, Level level) {
        return this.ingredient.test(input.item());
    }

    @Override
    public ItemStack assemble(Input input, HolderLookup.Provider registries) {
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

    public record Input(ItemStack item, int heat) implements RecipeInput {

        @Override
        public ItemStack getItem(int p_345528_) {
            if (p_345528_ != 0) {
                throw new IllegalArgumentException("No item for index " + p_345528_);
            } else {
                return this.item;
            }
        }

        @Override
        public int size() {
            return 1;
        }
    }

    public static class Active {
        private ResourceLocation lastRecipeId;
        private RecipeHolder<CrucibleRecipe> recipe;
        private float progress;

        public boolean hasRecipe() {
            return this.recipe != null;
        }

        public boolean updateWith(Level level, Input input) {
            if (!this.hasRecipe()) {
                // Set recipe based on input
                this.recipe = level.getRecipeManager().getRecipeFor(RecipeSetup.CRUCIBLE_TYPE.get(), input, level, this.lastRecipeId).orElse(null);
            }

            if (this.hasRecipe()) {
                // Check current recipe is its still valid
                if (this.recipe.value().matches(input, level)) {
                    if (this.isFinished() || input.heat < this.recipe.value().heat) {
                        // The recipe is still valid but has finished or fuel has ran out; no need to update.
                        return false;
                    }

                    // Progress the recipe.
                    if (this.isActive()) {
                        float heatBonusMultiplier = (input.heat / (float) this.recipe.value().heat) * ServerConfigs.getSmeltingHeatBonusMultiplier();
                        this.progress += (1.0f / this.recipe.value().smeltingTime) * heatBonusMultiplier;
                    }

                    return true;
                }

                // Input no longer matches the current recipe
                this.reset();
            }

            return false;
        }

        public void reset() {
            this.lastRecipeId = this.recipe.id();
            this.progress = 0;
            this.recipe = null;
        }

        public float getProgress() {
            if (!this.hasRecipe()) return 0;
            return this.progress;
        }

        public boolean isFinished() {
            if (!this.hasRecipe()) return false;
            return this.progress >= 1.0F;
        }

        public boolean isActive() {
            if (!this.hasRecipe()) return false;
            return this.progress < 1.0F;
        }

        public FluidStack createOutput() {
            return this.recipe.value().getFluidResult().copy();
        }

        public ItemStack createByproduct() {
            return this.recipe.value().byproduct().copy();
        }

        public int getProgressPercent() {
            if (!this.hasRecipe()) return 0;
            return (int) Math.ceil(this.progress * 100);
        }

        @Override
        public String toString() {
            if (!this.hasRecipe()) return "<x>";
            return this.recipe.id() + ": " + this.progress + " @ " + this.recipe.value().smeltingTime + " / tick";
        }

        public void serializeNbt(CompoundTag pTag, HolderLookup.Provider pRegistries) {
            pTag.putFloat("Progress", this.progress);
            pTag.putString("RecipeId", this.recipe.id().toString());
        }

        public void deserializeNbt(CompoundTag pTag, HolderLookup.Provider pRegistries) {
            this.progress = pTag.getFloat("Progress");
            this.recipe = pRegistries.asGetterLookup().get(Registries.RECIPE, ResourceKey.create(Registries.RECIPE, ResourceLocation.parse(pTag.getString("RecipeId")))).map(ref -> new RecipeHolder<>(ref.key().location(), (CrucibleRecipe) ref.value())).orElse(null);
        }
    }
}

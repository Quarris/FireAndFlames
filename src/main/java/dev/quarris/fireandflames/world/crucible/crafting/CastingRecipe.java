package dev.quarris.fireandflames.world.crucible.crafting;

import dev.quarris.fireandflames.setup.RecipeSetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

public abstract class CastingRecipe implements Recipe<CastingRecipe.Input> {

    public final ItemStack result;
    public final FluidIngredient fluidInput;
    public final Ingredient itemInput;
    public final int coolingTime;
    private final boolean consumeItem;
    // private final boolean copyData; ?

    protected CastingRecipe(ItemStack result, FluidIngredient fluidInput, Ingredient itemInput, int coolingTime, boolean consumeItem/*, boolean copyData*/) {
        this.result = result;
        this.fluidInput = fluidInput;
        this.itemInput = itemInput;
        this.coolingTime = coolingTime;
        this.consumeItem = consumeItem;
        //this.copyData = copyData;
    }

    @Override
    public boolean matches(Input input, Level level) {
        return this.fluidInput.test(input.fluid()) && this.itemInput.test(input.item());
    }

    @Override
    public ItemStack assemble(Input input, HolderLookup.Provider registries) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result.copy();
    }

    public ItemStack getResult() {
        return result.copy();
    }

    public FluidIngredient getFluidInput() {
        return fluidInput;
    }

    public Ingredient getItemInput() {
        return itemInput;
    }

    public int getCoolingTime() {
        return this.coolingTime;
    }

    public boolean consumesItem() {
        return this.consumeItem;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(this.itemInput);
        return ingredients;
    }

    public record Input(FluidStack fluid, ItemStack item) implements RecipeInput {

        @Override
        public ItemStack getItem(int index) {
            if (index != 0) {
                throw new IllegalArgumentException("No item for index " + index);
            } else {
                return this.item;
            }
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return this.fluid.isEmpty();
        }
    }
}

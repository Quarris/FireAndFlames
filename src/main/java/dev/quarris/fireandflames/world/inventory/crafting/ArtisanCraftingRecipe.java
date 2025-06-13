package dev.quarris.fireandflames.world.inventory.crafting;

import dev.quarris.fireandflames.setup.RecipeSetup;
import dev.quarris.fireandflames.util.recipe.IItemOutput;
import dev.quarris.fireandflames.util.recipe.ItemInput;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public record ArtisanCraftingRecipe(
    String group,
    ItemInput ingredient,
    IItemOutput result,
    IItemOutput byproduct
) implements Recipe<SingleRecipeInput> {

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return this.ingredient.matchesAmount(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return this.result.createItemStack();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result.createItemStack();
    }

    public ItemStack createByproduct() {
        return this.byproduct.createItemStack();
    }

    public ArtisanRecipeOutput createResults(SingleRecipeInput input, HolderLookup.Provider registries) {
        return new ArtisanRecipeOutput(this.ingredient.count().evaluateInt(), this.result().createItemStack(), this.byproduct().createItemStack());
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSetup.ARTISAN_CRAFTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeSetup.ARTISAN_CRAFTING_TYPE.get();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }
}

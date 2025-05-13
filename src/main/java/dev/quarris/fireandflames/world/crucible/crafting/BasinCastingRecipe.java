package dev.quarris.fireandflames.world.crucible.crafting;

import dev.quarris.fireandflames.setup.RecipeSetup;
import dev.quarris.fireandflames.util.recipe.FluidInput;
import dev.quarris.fireandflames.util.recipe.IItemOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class BasinCastingRecipe extends CastingRecipe {

    public BasinCastingRecipe(IItemOutput result, FluidInput fluidInput, Ingredient itemInput, int coolingTime, boolean consumeItem, boolean moveItem) {
        super(result, fluidInput, itemInput, coolingTime, consumeItem, moveItem);
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeSetup.BASIN_CASTING_TYPE.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSetup.BASIN_CASTING_SERIALIZER.get();
    }
}

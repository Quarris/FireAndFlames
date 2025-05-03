package dev.quarris.fireandflames.world.crucible.crafting;

import dev.quarris.fireandflames.setup.RecipeSetup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

public class BasinCastingRecipe extends CastingRecipe {

    public BasinCastingRecipe(ItemStack result, FluidIngredient fluidInput, int fluidInputAmount, Ingredient itemInput, int coolingTime, boolean consumeItem) {
        super(result, fluidInput, fluidInputAmount, itemInput, coolingTime, consumeItem);
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

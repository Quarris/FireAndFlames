package dev.quarris.fireandflames.world.crucible.crafting;

import dev.quarris.fireandflames.setup.RecipeSetup;
import dev.quarris.fireandflames.util.FluidInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

public class TableCastingRecipe extends CastingRecipe {

    public TableCastingRecipe(ItemStack result, FluidInput fluidInput, Ingredient itemInput, int coolingTime, boolean consumeItem) {
        super(result, fluidInput, itemInput, coolingTime, consumeItem);
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeSetup.TABLE_CASTING_TYPE.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSetup.TABLE_CASTING_SERIALIZER.get();
    }
}

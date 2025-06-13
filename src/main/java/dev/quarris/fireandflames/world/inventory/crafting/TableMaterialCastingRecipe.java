package dev.quarris.fireandflames.world.inventory.crafting;

import dev.quarris.fireandflames.data.config.number.INumberProvider;
import dev.quarris.fireandflames.setup.RecipeSetup;
import dev.quarris.fireandflames.util.recipe.IItemOutput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class TableMaterialCastingRecipe extends MaterialCastingRecipe {

    public TableMaterialCastingRecipe(IItemOutput result, INumberProvider fluidInputUnits, Ingredient itemInput, int coolingTime, boolean consumeInput, boolean moveItem) {
        super(result, fluidInputUnits, itemInput, coolingTime, consumeInput, moveItem);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSetup.TABLE_MATERIAL_CASTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeSetup.TABLE_MATERIAL_CASTING_TYPE.get();
    }
}

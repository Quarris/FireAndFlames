package dev.quarris.fireandflames.world.crucible.crafting;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record MeltingRecipeInput(EntityType<?> entity, boolean hasFluid) implements RecipeInput {

    @Override
    public ItemStack getItem(int pIndex) {
        throw new IllegalArgumentException("Melting Recipes don't take items");
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}

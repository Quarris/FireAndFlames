package dev.quarris.fireandflames.compat.jei.recipetypes;

import dev.quarris.fireandflames.world.inventory.crafting.ArtisanRecipeOutput;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Function;

public record JeiArtisanRecipe(Ingredient ingredient, Function<Ingredient, ArtisanRecipeOutput> output) {
}

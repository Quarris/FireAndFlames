package dev.quarris.fireandflames.compat.jei.recipetypes;

import dev.quarris.fireandflames.world.inventory.crafting.ArtisanRecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Function;

public record ArtisanRecipeDisplay(ResourceLocation id, Ingredient ingredient, ArtisanRecipeOutput output) {
}

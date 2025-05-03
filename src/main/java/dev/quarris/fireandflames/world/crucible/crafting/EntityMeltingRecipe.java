package dev.quarris.fireandflames.world.crucible.crafting;

import dev.quarris.fireandflames.setup.RecipeSetup;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record EntityMeltingRecipe(
    EntityTypePredicate entityPredicate,
    boolean requiresFluid,
    IFluidStackProvider result,
    float chance
) implements Recipe<MeltingRecipeInput> {

    @Override
    public boolean matches(MeltingRecipeInput input, Level level) {
        return level.getRandom().nextFloat() <= this.chance && this.entityPredicate.matches(input.entity()) && (!this.requiresFluid || input.hasFluid());
    }

    @Override
    public ItemStack assemble(MeltingRecipeInput input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;

    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSetup.ENTITY_MELTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeSetup.ENTITY_MELTING_TYPE.get();
    }
}

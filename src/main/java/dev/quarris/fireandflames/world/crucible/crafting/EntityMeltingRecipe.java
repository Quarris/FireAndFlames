package dev.quarris.fireandflames.world.crucible.crafting;

import dev.quarris.fireandflames.setup.RecipeSetup;
import dev.quarris.fireandflames.util.recipe.IFluidOutput;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record EntityMeltingRecipe(
    EntityTypePredicate entityPredicate,
    boolean requiresFluid,
    IFluidOutput result,
    float chance
) implements Recipe<EntityMeltingRecipe.Input> {

    @Override
    public boolean matches(Input input, Level level) {
        return level.getRandom().nextFloat() <= this.chance && this.entityPredicate.matches(input.entity()) && (!this.requiresFluid || input.hasFluid());
    }

    @Override
    public ItemStack assemble(Input input, HolderLookup.Provider registries) {
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
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSetup.ENTITY_MELTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeSetup.ENTITY_MELTING_TYPE.get();
    }

    public record Input(EntityType<?> entity, boolean hasFluid) implements RecipeInput {

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
}

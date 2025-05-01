package dev.quarris.fireandflames.data.recipes;

import dev.quarris.fireandflames.world.crucible.crafting.EntityMeltingRecipe;
import dev.quarris.fireandflames.world.crucible.crafting.IFluidRecipeOutput;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.Nullable;

public class EntityMeltingRecipeBuilder implements RecipeBuilder {

    private final EntityTypePredicate entityPredicate;
    private final IFluidRecipeOutput result;

    private boolean requiresFluid = true;
    private float chance = 1.0f;

    private EntityMeltingRecipeBuilder(
        EntityTypePredicate entityPredicate,
        FluidStack result
    ) {
        this(entityPredicate, new IFluidRecipeOutput.Direct(result));
    }

    private EntityMeltingRecipeBuilder(
        EntityTypePredicate entityPredicate,
        TagKey<Fluid> result,
        int amount
    ) {
        this(entityPredicate, new IFluidRecipeOutput.Tag(result, amount));
    }

    private EntityMeltingRecipeBuilder(
        EntityTypePredicate entityPredicate,
        IFluidRecipeOutput result
    ) {
        this.entityPredicate = entityPredicate;
        this.result = result;
    }

    public static EntityMeltingRecipeBuilder melt(EntityTypePredicate entity, FluidStack result) {
        return new EntityMeltingRecipeBuilder(entity, result);
    }

    public static EntityMeltingRecipeBuilder melt(EntityTypePredicate entity, TagKey<Fluid> result, int amount) {
        return new EntityMeltingRecipeBuilder(entity, result, amount);
    }

    public EntityMeltingRecipeBuilder requiresNoFluid() {
        this.requiresFluid = false;
        return this;
    }

    public EntityMeltingRecipeBuilder withChance(float chance) {
        this.chance = chance;
        return this;
    }

    public FluidStack getFluidResult() {
        return this.result.createFluid();
    }

    @Override
    public EntityMeltingRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        return this;
    }

    @Override
    public EntityMeltingRecipeBuilder group(@Nullable String groupName) {
        return this;
    }

    @Override
    public Item getResult() {
        return FluidUtil.getFilledBucket(this.getFluidResult()).getItem();
    }

    @Override
    public void save(RecipeOutput recipeOutput) {
        this.save(recipeOutput, getDefaultRecipeId(this.result));
    }

    @Override
    public void save(RecipeOutput recipeOutput, String id) {
        this.save(recipeOutput, ResourceLocation.parse(id));
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        EntityMeltingRecipe recipe = new EntityMeltingRecipe(this.entityPredicate, this.requiresFluid, this.result, this.chance);
        recipeOutput.accept(id, recipe, null);
    }

    public static ResourceLocation getDefaultRecipeId(IFluidRecipeOutput result) {
        ResourceLocation baseName;
        if (result instanceof IFluidRecipeOutput.Tag tag) {
            baseName = tag.tag().location();
        } else if (result instanceof IFluidRecipeOutput.Direct direct) {
            baseName = BuiltInRegistries.FLUID.getKey(direct.createFluid().getFluid());
        } else {
            throw new UnsupportedOperationException("Invalid fluid recipe output");
        }

        return baseName.withPrefix("melting/");
    }
}

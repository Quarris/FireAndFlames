package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.world.crucible.crafting.CrucibleRecipe;
import dev.quarris.fireandflames.world.crucible.crafting.CrucibleRecipeSerializer;
import dev.quarris.fireandflames.world.crucible.crafting.EntityMeltingRecipe;
import dev.quarris.fireandflames.world.crucible.crafting.EntityMeltingRecipeSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RecipeSetup {

    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, ModRef.ID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, ModRef.ID);

    // Recipe Types
    public static final DeferredHolder<RecipeType<?>, RecipeType<CrucibleRecipe>> CRUCIBLE_TYPE = RECIPE_TYPES.register("crucible", () -> RecipeType.simple(ModRef.res("crucible")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<EntityMeltingRecipe>> ENTITY_MELTING_TYPE = RECIPE_TYPES.register("entity_melting", () -> RecipeType.simple(ModRef.res("entity_melting")));

    // Recipe Serializers
    public static final DeferredHolder<RecipeSerializer<?>, CrucibleRecipeSerializer> CRUCIBLE_SERIALIZER = RECIPE_SERIALIZERS.register("crucible", CrucibleRecipeSerializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, EntityMeltingRecipeSerializer> ENTITY_MELTING_SERIALIZER = RECIPE_SERIALIZERS.register("entity_melting", EntityMeltingRecipeSerializer::new);


    public static void init(IEventBus modBus) {
        RECIPE_TYPES.register(modBus);
        RECIPE_SERIALIZERS.register(modBus);
    }
}

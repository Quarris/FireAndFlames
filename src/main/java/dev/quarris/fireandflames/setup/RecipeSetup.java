package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.world.crucible.crafting.*;
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
    public static final DeferredHolder<RecipeType<?>, RecipeType<BasinCastingRecipe>> BASIN_CASTING_TYPE = RECIPE_TYPES.register("basin_casting", () -> RecipeType.simple(ModRef.res("basin_casting")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<TableCastingRecipe>> TABLE_CASTING_TYPE = RECIPE_TYPES.register("table_casting", () -> RecipeType.simple(ModRef.res("table_casting")));

    // Recipe Serializers
    public static final DeferredHolder<RecipeSerializer<?>, CrucibleRecipeSerializer> CRUCIBLE_SERIALIZER = RECIPE_SERIALIZERS.register("crucible", CrucibleRecipeSerializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, EntityMeltingRecipeSerializer> ENTITY_MELTING_SERIALIZER = RECIPE_SERIALIZERS.register("entity_melting", EntityMeltingRecipeSerializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, CastingRecipeSerializer<?>> BASIN_CASTING_SERIALIZER = RECIPE_SERIALIZERS.register("basin_casting", () -> new CastingRecipeSerializer<>(BasinCastingRecipe::new, true));
    public static final DeferredHolder<RecipeSerializer<?>, CastingRecipeSerializer<?>> TABLE_CASTING_SERIALIZER = RECIPE_SERIALIZERS.register("table_casting", () -> new CastingRecipeSerializer<>(TableCastingRecipe::new, false));


    public static void init(IEventBus modBus) {
        RECIPE_TYPES.register(modBus);
        RECIPE_SERIALIZERS.register(modBus);
    }
}

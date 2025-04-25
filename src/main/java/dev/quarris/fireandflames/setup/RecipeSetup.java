package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.world.item.crafting.CrucibleRecipe;
import dev.quarris.fireandflames.world.item.crafting.CrucibleSmeltingSerializer;
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

    // Recipe Serializers
    public static final DeferredHolder<RecipeSerializer<?>, CrucibleSmeltingSerializer> CRUCIBLE_SERIALIZER = RECIPE_SERIALIZERS.register("crucible", CrucibleSmeltingSerializer::new);


    public static void init(IEventBus modBus) {
        RECIPE_TYPES.register(modBus);
        RECIPE_SERIALIZERS.register(modBus);
    }
}

package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.world.inventory.crafting.*;
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
    public static final DeferredHolder<RecipeType<?>, RecipeType<AlloyingRecipe>> ALLOYING_TYPE = RECIPE_TYPES.register("alloying", () -> RecipeType.simple(ModRef.res("alloying")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<SmithingAnvilRecipe>> SMITHING_ANVIL_TYPE = RECIPE_TYPES.register("smithing_anvil", () -> RecipeType.simple(ModRef.res("smithing_anvil")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<TableMaterialCastingRecipe>> TABLE_MATERIAL_CASTING_TYPE = RECIPE_TYPES.register("table_material_casting", () -> RecipeType.simple(ModRef.res("table_material_casting")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<ArtisanCraftingRecipe>> ARTISAN_CRAFTING_TYPE = RECIPE_TYPES.register("artisan_crafting", () -> RecipeType.simple(ModRef.res("artisan_crafting")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<MaterialArtisanCraftingRecipe>> MATERIAL_ARTISAN_CRAFTING_TYPE = RECIPE_TYPES.register("material_artisan_crafting", () -> RecipeType.simple(ModRef.res("material_artisan_crafting")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<FamilyArtisanCraftingRecipe>> FAMILY_ARTISAN_CRAFTING_TYPE = RECIPE_TYPES.register("family_artisan_crafting", () -> RecipeType.simple(ModRef.res("family_artisan_crafting")));

    // Recipe Serializers
    public static final DeferredHolder<RecipeSerializer<?>, CrucibleRecipeSerializer> CRUCIBLE_SERIALIZER = RECIPE_SERIALIZERS.register("crucible", CrucibleRecipeSerializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, EntityMeltingRecipeSerializer> ENTITY_MELTING_SERIALIZER = RECIPE_SERIALIZERS.register("entity_melting", EntityMeltingRecipeSerializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, BasicCastingRecipeSerializer<?>> BASIN_CASTING_SERIALIZER = RECIPE_SERIALIZERS.register("basin_casting", () -> new BasicCastingRecipeSerializer<>(BasinCastingRecipe::new, true));
    public static final DeferredHolder<RecipeSerializer<?>, BasicCastingRecipeSerializer<?>> TABLE_CASTING_SERIALIZER = RECIPE_SERIALIZERS.register("table_casting", () -> new BasicCastingRecipeSerializer<>(TableCastingRecipe::new, false));
    public static final DeferredHolder<RecipeSerializer<?>, AlloyingRecipeSerializer> ALLOYING_SERIALIZER = RECIPE_SERIALIZERS.register("alloying", AlloyingRecipeSerializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, SmithingAnvilRecipeSerializer> SMITHING_ANVIL_SERIALIZER = RECIPE_SERIALIZERS.register("smithing_anvil", SmithingAnvilRecipeSerializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, MaterialCastingRecipeSerializer<?>> TABLE_MATERIAL_CASTING_SERIALIZER = RECIPE_SERIALIZERS.register("table_material_casting", () -> new MaterialCastingRecipeSerializer<>(TableMaterialCastingRecipe::new, false));
    public static final DeferredHolder<RecipeSerializer<?>, ArtisanCraftingRecipeSerializer> ARTISAN_CRAFTING_SERIALIZER = RECIPE_SERIALIZERS.register("artisan_crafting", ArtisanCraftingRecipeSerializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, MaterialArtisanCraftingRecipeSerializer> MATERIAL_ARTISAN_CRAFTING_SERIALIZER = RECIPE_SERIALIZERS.register("material_artisan_crafting", MaterialArtisanCraftingRecipeSerializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, FamilyArtisanCraftingRecipeSerializer> FAMILY_ARTISAN_CRAFTING_SERIALIZER = RECIPE_SERIALIZERS.register("family_artisan_crafting", FamilyArtisanCraftingRecipeSerializer::new);


    public static void init(IEventBus modBus) {
        RECIPE_TYPES.register(modBus);
        RECIPE_SERIALIZERS.register(modBus);
    }
}

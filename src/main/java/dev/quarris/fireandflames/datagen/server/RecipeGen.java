package dev.quarris.fireandflames.datagen.server;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.config.number.ConfigNumber;
import dev.quarris.fireandflames.data.config.number.ConstantNumber;
import dev.quarris.fireandflames.data.config.number.MultiplyNumber;
import dev.quarris.fireandflames.data.recipe.*;
import dev.quarris.fireandflames.setup.*;
import dev.quarris.fireandflames.util.recipe.FluidInput;
import dev.quarris.fireandflames.util.recipe.IFluidOutput;
import dev.quarris.fireandflames.util.recipe.IItemOutput;
import dev.quarris.fireandflames.util.recipe.ItemInput;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.concurrent.CompletableFuture;

public class RecipeGen extends RecipeProvider {

    public RecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void buildRecipes(RecipeOutput output, HolderLookup.Provider registries) {
        shapedRecipes(output, registries);
        shapelessRecipes(output, registries);
        smeltingRecipes(output, registries);
        blastingRecipes(output, registries);
        crucibleRecipes(output, registries);
        meltingRecipes(output, registries);
        castingRecipes(output, registries);
        alloyingRecipes(output, registries);
        smithingAnvilRecipes(output, registries);
        metalRecipes(output, registries);
        artisanRecipes(output, registries);
    }

    private static void artisanRecipes(RecipeOutput output, HolderLookup.Provider registries) {
        ArtisanCraftingRecipeBuilder.builder(new IItemOutput.Stack(Items.BOOK), new ItemInput(Items.PAPER, 3))
            .save(output);

        FamilyArtisanCraftingRecipeBuilder.builder(Ingredient.of(ItemTags.PLANKS))
            .variant(BlockFamily.Variant.STAIRS)
            .variant(BlockFamily.Variant.SLAB, new ConstantNumber(1), new ConstantNumber(2))
            .variant(BlockFamily.Variant.FENCE, new ConstantNumber(1), new ConstantNumber(2))
            .variant(BlockFamily.Variant.CUSTOM_FENCE, new ConstantNumber(1), new ConstantNumber(2))
            .variant(BlockFamily.Variant.FENCE_GATE, new ConstantNumber(4), new ConstantNumber(1), new IItemOutput.Stack(Items.STICK, 2))
            .variant(BlockFamily.Variant.CUSTOM_FENCE_GATE, new ConstantNumber(4), new ConstantNumber(1), new IItemOutput.Stack(Items.STICK, 2))
            .variant(BlockFamily.Variant.DOOR, new ConstantNumber(2), new ConstantNumber(1))
            .variant(BlockFamily.Variant.BUTTON, new ConstantNumber(1), new ConstantNumber(4))
            .variant(BlockFamily.Variant.PRESSURE_PLATE, new ConstantNumber(1), new ConstantNumber(1))
            .variant(BlockFamily.Variant.SIGN, new ConstantNumber(2), new ConstantNumber(1))
            .variant(BlockFamily.Variant.TRAPDOOR, new ConstantNumber(2), new ConstantNumber(4))
            .saveAtDefaultPath(output, "planks");

        MaterialArtisanCraftingRecipeBuilder.builder(ToolItemSetup.TOOL_SHAPE, 2)
            .save(output);
        MaterialArtisanCraftingRecipeBuilder.builder(ToolItemSetup.BLADE_SHAPE, 2)
            .save(output);
        MaterialArtisanCraftingRecipeBuilder.builder(ToolItemSetup.MISC_SHAPE, 1)
            .save(output);
    }

    private static void metalRecipes(RecipeOutput output, HolderLookup.Provider registries) {
        metalRecipe(output, "iron", TagSetup.FluidTags.MOLTEN_IRON, Tags.Items.STORAGE_BLOCKS_RAW_IRON, Tags.Items.RAW_MATERIALS_IRON, Tags.Items.STORAGE_BLOCKS_IRON, Tags.Items.INGOTS_IRON, Tags.Items.NUGGETS_IRON);
        metalRecipe(output, "gold", TagSetup.FluidTags.MOLTEN_GOLD, Tags.Items.STORAGE_BLOCKS_RAW_GOLD, Tags.Items.RAW_MATERIALS_GOLD, Tags.Items.STORAGE_BLOCKS_GOLD, Tags.Items.INGOTS_GOLD, Tags.Items.NUGGETS_GOLD);
        metalRecipe(output, "copper", TagSetup.FluidTags.MOLTEN_COPPER, Tags.Items.STORAGE_BLOCKS_RAW_COPPER, Tags.Items.RAW_MATERIALS_COPPER, Tags.Items.STORAGE_BLOCKS_COPPER, Tags.Items.INGOTS_COPPER, null);
        metalRecipe(output, "ancient_debris", TagSetup.FluidTags.MOLTEN_ANCIENT_DEBRIS, null, Tags.Items.ORES_NETHERITE_SCRAP, null, null, null);
        metalRecipe(output, "netherite", TagSetup.FluidTags.MOLTEN_NETHERITE, null, null, Tags.Items.STORAGE_BLOCKS_NETHERITE, Tags.Items.INGOTS_NETHERITE, null);
    }

    private static void alloyingRecipes(RecipeOutput output, HolderLookup.Provider registries) {
        AlloyingRecipeBuilder.alloy(new IFluidOutput.Tag(TagSetup.FluidTags.MOLTEN_NETHERITE, 1))
            .requires(new FluidInput(TagSetup.FluidTags.MOLTEN_ANCIENT_DEBRIS, 4))
            .requires(new FluidInput(TagSetup.FluidTags.MOLTEN_GOLD, 4))
            .save(output, ModRef.res("crucible/alloying/netherite_from_scrap_and_gold"));
    }

    private static void smithingAnvilRecipes(RecipeOutput output, HolderLookup.Provider registries) {
        Item pickaxeHead = BuiltInRegistries.ITEM.get(PartTypeSetup.PICKAXE_HEAD.getId());
        Item axeHead = BuiltInRegistries.ITEM.get(PartTypeSetup.AXE_HEAD.getId());
        Item shovelHead = BuiltInRegistries.ITEM.get(PartTypeSetup.SHOVEL_HEAD.getId());
        Item hoeHead = BuiltInRegistries.ITEM.get(PartTypeSetup.HOE_HEAD.getId());
        Item swordBlade = BuiltInRegistries.ITEM.get(PartTypeSetup.SWORD_BLADE.getId());
        Item binding = BuiltInRegistries.ITEM.get(PartTypeSetup.BINDING.getId());
        Item handle = BuiltInRegistries.ITEM.get(PartTypeSetup.HANDLE.getId());
        Item wideGuard = BuiltInRegistries.ITEM.get(PartTypeSetup.WIDE_GUARD.getId());

        SmithingAnvilRecipeBuilder.builder(Ingredient.of(new ItemStack(ToolItemSetup.TOOL_SHAPE.get())))
            .creates(new ItemStack(pickaxeHead))
            .creates(new ItemStack(axeHead))
            .creates(new ItemStack(shovelHead))
            .creates(new ItemStack(hoeHead))
            .save(output);

        SmithingAnvilRecipeBuilder.builder(Ingredient.of(new ItemStack(ToolItemSetup.BLADE_SHAPE.get())))
            .creates(new ItemStack(swordBlade))
            .save(output);

        SmithingAnvilRecipeBuilder.builder(Ingredient.of(new ItemStack(ToolItemSetup.MISC_SHAPE.get())))
            .creates(new ItemStack(binding))
            .creates(new ItemStack(handle))
            .creates(new ItemStack(wideGuard))
            .save(output);
    }

    private static void castingRecipes(RecipeOutput output, HolderLookup.Provider registries) {
        MaterialCastingRecipeBuilder.table(1, new IItemOutput.Stack(ToolItemSetup.MISC_SHAPE.get()))
            .withItemInput(ItemSetup.MISC_SHAPE_CAST.get())
            .saveFnf(output);

        MaterialCastingRecipeBuilder.table(1, new IItemOutput.Stack(ToolItemSetup.BLADE_SHAPE.get()))
            .withItemInput(ItemSetup.BLADE_SHAPE_CAST.get())
            .saveFnf(output);

        MaterialCastingRecipeBuilder.table(1, new IItemOutput.Stack(ToolItemSetup.TOOL_SHAPE.get()))
            .withItemInput(ItemSetup.TOOL_SHAPE_CAST.get())
            .saveFnf(output);

        CastingRecipeBuilder.table(FluidIngredient.tag(TagSetup.FluidTags.MOLTEN_GOLD), new MultiplyNumber(new ConstantNumber(2), ConfigNumber.ConfigValue.INGOT_MB.toProvider()), new IItemOutput.Stack(ItemSetup.INGOT_CAST.get()))
            .withItemInput(Ingredient.of(Tags.Items.INGOTS))
            .moveItem(true)
            .coolingTime(240)
            .consumesInput(true)
            .saveFnf(output);

        CastingRecipeBuilder.table(FluidIngredient.tag(TagSetup.FluidTags.MOLTEN_GOLD), new MultiplyNumber(new ConstantNumber(2), ConfigNumber.ConfigValue.INGOT_MB.toProvider()), new IItemOutput.Stack(ItemSetup.NUGGET_CAST.get()))
            .withItemInput(Ingredient.of(Tags.Items.NUGGETS))
            .moveItem(true)
            .coolingTime(240)
            .consumesInput(true)
            .saveFnf(output);

        CastingRecipeBuilder.table(FluidIngredient.tag(TagSetup.FluidTags.MOLTEN_GOLD), new MultiplyNumber(new ConstantNumber(2), ConfigNumber.ConfigValue.INGOT_MB.toProvider()), new IItemOutput.Stack(ItemSetup.TOOL_SHAPE_CAST.get()))
            .withItemInput(Ingredient.of(ToolItemSetup.TOOL_SHAPE.get()))
            .moveItem(true)
            .coolingTime(240)
            .consumesInput(true)
            .saveFnf(output);

        CastingRecipeBuilder.table(FluidIngredient.tag(TagSetup.FluidTags.MOLTEN_GOLD), new MultiplyNumber(new ConstantNumber(2), ConfigNumber.ConfigValue.INGOT_MB.toProvider()), new IItemOutput.Stack(ItemSetup.BLADE_SHAPE_CAST.get()))
            .withItemInput(Ingredient.of(ToolItemSetup.BLADE_SHAPE.get()))
            .moveItem(true)
            .coolingTime(240)
            .consumesInput(true)
            .saveFnf(output);

        CastingRecipeBuilder.table(FluidIngredient.tag(TagSetup.FluidTags.MOLTEN_GOLD), new MultiplyNumber(new ConstantNumber(2), ConfigNumber.ConfigValue.INGOT_MB.toProvider()), new IItemOutput.Stack(ItemSetup.MISC_SHAPE_CAST.get()))
            .withItemInput(Ingredient.of(ToolItemSetup.MISC_SHAPE.get()))
            .moveItem(true)
            .coolingTime(240)
            .consumesInput(true)
            .saveFnf(output);
    }

    private static void meltingRecipes(RecipeOutput output, HolderLookup.Provider registries) {
        EntityMeltingRecipeBuilder.melt(EntityTypePredicate.of(EntityType.IRON_GOLEM), TagSetup.FluidTags.MOLTEN_IRON, new MultiplyNumber(ConfigNumber.ConfigValue.NUGGET_MB.toProvider()))
            .requiresFluid()
            .save(output, ModRef.res("crucible/melting/iron_from_iron_golem"));

        EntityMeltingRecipeBuilder.melt(EntityTypePredicate.of(EntityType.SNOW_GOLEM), FluidTags.WATER, 50)
            .heat(300)
            .save(output, ModRef.res("crucible/melting/water_from_snow_golem"));
    }

    private static void crucibleRecipes(RecipeOutput output, HolderLookup.Provider registries) {
        CrucibleRecipeBuilder.smelting(new FluidStack(Fluids.WATER, 1000), Ingredient.of(Items.ICE), 100)
            .byproduct(new ItemStack(Items.STICK))
            .save(output, ModRef.res("crucible/water_from_ice"));

        CrucibleRecipeBuilder.smelting(FluidTags.LAVA, 1000, Ingredient.of(Items.OBSIDIAN), 200)
            .save(output, ModRef.res("crucible/lava_from_obsidian"));
    }

    public static void smeltingRecipes(RecipeOutput output, HolderLookup.Provider registries) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ItemSetup.FIRE_CLAY_BALL.get()), RecipeCategory.MISC, ItemSetup.FIRE_BRICK.get(), 0.15f, 200)
            .unlockedBy("has_fire_clay_ball", has(ItemSetup.FIRE_CLAY_BALL.get()))
            .save(output, ModRef.res("smelting/fire_brick_smelting"));
    }

    public static void blastingRecipes(RecipeOutput output, HolderLookup.Provider registries) {
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(ItemSetup.FIRE_CLAY_BALL.get()), RecipeCategory.MISC, ItemSetup.FIRE_BRICK.get(), 0.15f, 100)
            .unlockedBy("has_fire_clay_ball", has(ItemSetup.FIRE_CLAY_BALL.get()))
            .save(output, ModRef.res("blasting/fire_brick_blasting"));
    }

    public static void shapedRecipes(RecipeOutput output, HolderLookup.Provider registries) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockSetup.FIRE_CLAY.get())
            .pattern("CC")
            .pattern("CC")
            .define('C', ItemSetup.FIRE_CLAY_BALL.get())
            .unlockedBy("has_fire_clay_ball", has(ItemSetup.FIRE_CLAY_BALL.get()))
            .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockSetup.FIRE_BRICKS.get())
            .pattern("BB")
            .pattern("BB")
            .define('B', ItemSetup.FIRE_BRICK.get())
            .unlockedBy("has_fire_brick", has(ItemSetup.FIRE_BRICK.get()))
            .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockSetup.CRUCIBLE_WINDOW.get())
            .pattern(" B ")
            .pattern("BGB")
            .pattern(" B ")
            .define('B', ItemSetup.FIRE_BRICK.get())
            .define('G', Tags.Items.GLASS_BLOCKS)
            .unlockedBy("has_fire_brick", has(ItemSetup.FIRE_BRICK.get()))
            .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
            .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockSetup.CRUCIBLE_DRAIN.get())
            .pattern("B B")
            .pattern("B B")
            .pattern("B B")
            .define('B', ItemSetup.FIRE_BRICK.get())
            .unlockedBy("has_fire_brick", has(ItemSetup.FIRE_BRICK.get()))
            .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockSetup.CASTING_BASIN.get())
            .pattern("B B")
            .pattern("B B")
            .pattern("BBB")
            .define('B', ItemSetup.FIRE_BRICK.get())
            .unlockedBy("has_fire_brick", has(ItemSetup.FIRE_BRICK.get()))
            .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockSetup.CASTING_TABLE.get())
            .pattern("BBB")
            .pattern("B B")
            .pattern("B B")
            .define('B', ItemSetup.FIRE_BRICK.get())
            .unlockedBy("has_fire_brick", has(ItemSetup.FIRE_BRICK.get()))
            .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockSetup.CRUCIBLE_TANK.get())
            .pattern("BBB")
            .pattern("BGB")
            .pattern("BBB")
            .define('B', ItemSetup.FIRE_BRICK.get())
            .define('G', Tags.Items.GLASS_BLOCKS)
            .unlockedBy("has_fire_brick", has(ItemSetup.FIRE_BRICK.get()))
            .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
            .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockSetup.CRUCIBLE_BURNER.get())
            .pattern("BBB")
            .pattern("BFB")
            .pattern("BBB")
            .define('B', ItemSetup.FIRE_BRICK.get())
            .define('F', Items.FLINT)
            .unlockedBy("has_fire_brick", has(ItemSetup.FIRE_BRICK.get()))
            .unlockedBy("has_flint", has(Items.FLINT))
            .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockSetup.CRUCIBLE_FAWSIT.get())
            .pattern("B B")
            .pattern(" B ")
            .define('B', ItemSetup.FIRE_BRICK.get())
            .unlockedBy("has_fire_brick", has(ItemSetup.FIRE_BRICK.get()))
            .save(output);
    }

    public static void shapelessRecipes(RecipeOutput output, HolderLookup.Provider registries) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, BlockSetup.CRUCIBLE_WINDOW.get())
            .requires(BlockSetup.FIRE_BRICKS.get())
            .requires(Tags.Items.GLASS_BLOCKS)
            .unlockedBy("has_fire_bricks", has(BlockSetup.FIRE_BRICKS.get()))
            .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
            .save(output, ModRef.res("crucible_window_shapeless"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemSetup.FIRE_CLAY_BALL.get())
            .requires(Tags.Items.SANDS_RED)
            .requires(Tags.Items.SANDS_RED)
            .requires(Tags.Items.SANDS_RED)
            .requires(Tags.Items.SANDS_RED)
            .requires(Items.CLAY_BALL)
            .unlockedBy("has_sand", has(Tags.Items.SANDS_RED))
            .unlockedBy("has_clay", has(Items.CLAY_BALL))
            .save(output, ModRef.res("fire_clay_ball"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemSetup.FIRE_CLAY_BALL.get(), 2)
            .requires(Tags.Items.SANDS)
            .requires(Tags.Items.SANDS)
            .requires(Tags.Items.SANDS)
            .requires(Tags.Items.SANDS)
            .requires(Tags.Items.DUSTS_REDSTONE)
            .requires(Items.CLAY_BALL)
            .requires(Items.CLAY_BALL)
            .unlockedBy("has_sand", has(Tags.Items.SANDS))
            .unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
            .unlockedBy("has_clay", has(Items.CLAY_BALL))
            .save(output, ModRef.res("fire_clay_ball_from_redstone"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ItemSetup.FIRE_CLAY_BALL.get(), 8)
            .requires(Tags.Items.SANDS_RED)
            .requires(Tags.Items.SANDS_RED)
            .requires(Tags.Items.SANDS_RED)
            .requires(Tags.Items.SANDS_RED)
            .requires(Items.CLAY_BALL)
            .requires(Items.CLAY_BALL)
            .requires(Items.CLAY_BALL)
            .requires(Items.CLAY_BALL)
            .requires(Items.BLAZE_POWDER)
            .unlockedBy("has_sand", has(Tags.Items.SANDS_RED))
            .unlockedBy("has_clay", has(Items.CLAY_BALL))
            .unlockedBy("has_blaze_powder", has(Items.BLAZE_POWDER))
            .save(output, ModRef.res("fire_clay_ball_from_blaze_powder"));
    }

    public static void metalRecipe(RecipeOutput output, String name, TagKey<Fluid> fluidTag, TagKey<Item> rawBlockTag, TagKey<Item> rawItemTag, TagKey<Item> blockTag, TagKey<Item> ingotTag, TagKey<Item> nuggetTag) {
        if (blockTag != null) {
            CastingRecipeBuilder.basin(FluidIngredient.tag(fluidTag), ConfigNumber.ConfigValue.BLOCK_MB.toProvider(), new IItemOutput.Tag(blockTag))
                .coolingTime(20 * 3 * 5)
                .saveFnf(output);

            CrucibleRecipeBuilder.smelting(fluidTag, ConfigNumber.ConfigValue.BLOCK_MB.toProvider(), Ingredient.of(blockTag), 900)
                .heat(1100)
                .save(output, ModRef.res("crucible/" + name + "_from_block"));
        }

        if (ingotTag != null) {
            CastingRecipeBuilder.table(FluidIngredient.tag(fluidTag), ConfigNumber.ConfigValue.INGOT_MB.toProvider(), new IItemOutput.Tag(ingotTag))
                .withItemInput(Ingredient.of(ItemSetup.INGOT_CAST))
                .coolingTime(20 * 3)
                .saveFnf(output);

            CrucibleRecipeBuilder.smelting(fluidTag, ConfigNumber.ConfigValue.INGOT_MB.toProvider(), Ingredient.of(ingotTag), 100)
                .save(output, ModRef.res("crucible/" + name + "_from_ingot"));
        }


        if (rawItemTag != null) {
            CrucibleRecipeBuilder.smelting(fluidTag, new MultiplyNumber(ConfigNumber.ConfigValue.INGOT_MB.toProvider(), ConfigNumber.ConfigValue.ORE_MULTIPLIER.toProvider()), Ingredient.of(rawItemTag), 100)
                .save(output, ModRef.res("crucible/" + name + "_from_raw"));
        }

        if (rawBlockTag != null) {
            CrucibleRecipeBuilder.smelting(fluidTag, new MultiplyNumber(ConfigNumber.ConfigValue.BLOCK_MB.toProvider(), ConfigNumber.ConfigValue.ORE_MULTIPLIER.toProvider()), Ingredient.of(rawBlockTag), 900)
                .heat(1100)
                .save(output, ModRef.res("crucible/" + name + "_from_raw_block"));
        }

        if (nuggetTag != null) {
            CastingRecipeBuilder.table(FluidIngredient.tag(fluidTag), ConfigNumber.ConfigValue.NUGGET_MB.toProvider(), new IItemOutput.Tag(nuggetTag))
                .withItemInput(Ingredient.of(ItemSetup.NUGGET_CAST))
                .coolingTime(20 * 3)
                .saveFnf(output);

            CrucibleRecipeBuilder.smelting(fluidTag, ConfigNumber.ConfigValue.NUGGET_MB.toProvider(), Ingredient.of(nuggetTag), 100)
                .save(output, ModRef.res("crucible/" + name + "_from_nugget"));
        }


    }
}

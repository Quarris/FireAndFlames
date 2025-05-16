package dev.quarris.fireandflames.datagen.server;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.config.number.ConfigNumber;
import dev.quarris.fireandflames.data.config.number.ConstantNumber;
import dev.quarris.fireandflames.data.config.number.MultiplyNumber;
import dev.quarris.fireandflames.data.recipes.AlloyingRecipeBuilder;
import dev.quarris.fireandflames.data.recipes.CastingRecipeBuilder;
import dev.quarris.fireandflames.data.recipes.CrucibleRecipeBuilder;
import dev.quarris.fireandflames.data.recipes.EntityMeltingRecipeBuilder;
import dev.quarris.fireandflames.setup.BlockSetup;
import dev.quarris.fireandflames.setup.ItemSetup;
import dev.quarris.fireandflames.setup.TagSetup;
import dev.quarris.fireandflames.util.recipe.FluidInput;
import dev.quarris.fireandflames.util.recipe.IFluidOutput;
import dev.quarris.fireandflames.util.recipe.IItemOutput;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.concurrent.CompletableFuture;

public class RecipesGen extends RecipeProvider {

    public RecipesGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void buildRecipes(RecipeOutput pOutput) {
        shapedRecipes(pOutput);
        shapelessRecipes(pOutput);
        smeltingRecipes(pOutput);
        blastingRecipes(pOutput);
        crucibleRecipes(pOutput);
        meltingRecipes(pOutput);
        castingRecipes(pOutput);
        alloyingRecipes(pOutput);
        metalRecipes(pOutput);
    }

    private static void metalRecipes(RecipeOutput output) {
        metalRecipe(output, "iron", TagSetup.FluidTags.MOLTEN_IRON, Tags.Items.STORAGE_BLOCKS_RAW_IRON, Tags.Items.RAW_MATERIALS_IRON, Tags.Items.STORAGE_BLOCKS_IRON, Tags.Items.INGOTS_IRON, Tags.Items.NUGGETS_IRON);
        metalRecipe(output, "gold", TagSetup.FluidTags.MOLTEN_GOLD, Tags.Items.STORAGE_BLOCKS_RAW_GOLD, Tags.Items.RAW_MATERIALS_GOLD, Tags.Items.STORAGE_BLOCKS_GOLD, Tags.Items.INGOTS_GOLD, Tags.Items.NUGGETS_GOLD);
        metalRecipe(output, "copper", TagSetup.FluidTags.MOLTEN_COPPER, Tags.Items.STORAGE_BLOCKS_RAW_COPPER, Tags.Items.RAW_MATERIALS_COPPER, Tags.Items.STORAGE_BLOCKS_COPPER, Tags.Items.INGOTS_COPPER, null);
        metalRecipe(output, "ancient_debris", TagSetup.FluidTags.MOLTEN_ANCIENT_DEBRIS, null, Tags.Items.ORES_NETHERITE_SCRAP, null, null, null);
        metalRecipe(output, "netherite", TagSetup.FluidTags.MOLTEN_NETHERITE, null, null, Tags.Items.STORAGE_BLOCKS_NETHERITE, Tags.Items.INGOTS_NETHERITE, null);
    }

    private static void alloyingRecipes(RecipeOutput pOutput) {
        AlloyingRecipeBuilder.alloy(new IFluidOutput.Stack(NeoForgeMod.MILK.get(), 10))
            .requires(new FluidInput(Tags.Fluids.LAVA, 10))
            .requires(new FluidInput(Fluids.WATER, 2))
            .save(pOutput, ModRef.res("crucible/alloying/milk_from_lava_and_water"));

        AlloyingRecipeBuilder.alloy(new IFluidOutput.Tag(TagSetup.FluidTags.MOLTEN_NETHERITE, 1))
            .requires(new FluidInput(TagSetup.FluidTags.MOLTEN_ANCIENT_DEBRIS, 4))
            .requires(new FluidInput(TagSetup.FluidTags.MOLTEN_GOLD, 4))
            .save(pOutput, ModRef.res("crucible/alloying/netherite_from_scrap_and_gold"));

        AlloyingRecipeBuilder.alloy(
                new IFluidOutput.Tag(TagSetup.FluidTags.MOLTEN_NETHERITE, 1),
                new IFluidOutput.Tag(TagSetup.FluidTags.MOLTEN_GOLD, 1),
                new IFluidOutput.Tag(TagSetup.FluidTags.MOLTEN_ANCIENT_DEBRIS, 1)
            )
            .requires(new FluidInput(TagSetup.FluidTags.MOLTEN_ANCIENT_DEBRIS, 4))
            .requires(new FluidInput(TagSetup.FluidTags.MOLTEN_IRON, 4))
            .requires(new FluidInput(TagSetup.FluidTags.MOLTEN_GOLD, 4))
            .requires(new FluidInput(TagSetup.FluidTags.MOLTEN_COPPER, 4))
            .requires(new FluidInput(Tags.Fluids.LAVA, 4))
            .requires(new FluidInput(Tags.Fluids.WATER, 4))
            .requires(new FluidInput(Tags.Fluids.MILK, 4))
            .save(pOutput, ModRef.res("crucible/alloying/test"));
    }

    private static void castingRecipes(RecipeOutput pOutput) {
        CastingRecipeBuilder.table(FluidIngredient.tag(TagSetup.FluidTags.MOLTEN_GOLD), new MultiplyNumber(new ConstantNumber(2), ConfigNumber.ConfigValue.INGOT_MB.toProvider()), new IItemOutput.Stack(ItemSetup.INGOT_CAST.get()))
            .withItemInput(Ingredient.of(Tags.Items.INGOTS))
            .moveItem(true)
            .coolingTime(240)
            .saveFnf(pOutput);

        CastingRecipeBuilder.table(FluidIngredient.tag(TagSetup.FluidTags.MOLTEN_GOLD), new MultiplyNumber(new ConstantNumber(2), ConfigNumber.ConfigValue.INGOT_MB.toProvider()), new IItemOutput.Stack(ItemSetup.NUGGET_CAST.get()))
            .withItemInput(Ingredient.of(Tags.Items.NUGGETS))
            .moveItem(true)
            .coolingTime(240)
            .saveFnf(pOutput);
    }

    private static void meltingRecipes(RecipeOutput pOutput) {
        EntityMeltingRecipeBuilder.melt(EntityTypePredicate.of(EntityType.PLAYER), new FluidStack(Fluids.LAVA, 5))
            .withChance(0.1f)
            .save(pOutput, ModRef.res("crucible/melting/lava_from_player"));

        EntityMeltingRecipeBuilder.melt(EntityTypePredicate.of(EntityType.SHEEP), FluidTags.WATER, 50)
            .requiresNoFluid()
            .save(pOutput, ModRef.res("crucible/melting/water_from_sheep"));
    }

    private static void crucibleRecipes(RecipeOutput pOutput) {
        CrucibleRecipeBuilder.smelting(new FluidStack(Fluids.WATER, 1000), Ingredient.of(Items.ICE), 100)
            .byproduct(new ItemStack(Items.STICK))
            .save(pOutput, ModRef.res("crucible/water_from_ice"));

        CrucibleRecipeBuilder.smelting(FluidTags.LAVA, 1000, Ingredient.of(Items.OBSIDIAN), 200)
            .save(pOutput, ModRef.res("crucible/lava_from_obsidian"));
    }

    public static void smeltingRecipes(RecipeOutput pOutput) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ItemSetup.FIRE_CLAY_BALL.get()), RecipeCategory.MISC, ItemSetup.FIRE_BRICK.get(), 0.15f, 200)
            .unlockedBy("has_fire_clay_ball", has(ItemSetup.FIRE_CLAY_BALL.get()))
            .save(pOutput, ModRef.res("smelting/fire_brick_smelting"));
    }

    public static void blastingRecipes(RecipeOutput pOutput) {
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(ItemSetup.FIRE_CLAY_BALL.get()), RecipeCategory.MISC, ItemSetup.FIRE_BRICK.get(), 0.15f, 100)
            .unlockedBy("has_fire_clay_ball", has(ItemSetup.FIRE_CLAY_BALL.get()))
            .save(pOutput, ModRef.res("blasting/fire_brick_blasting"));
    }

    public static void shapedRecipes(RecipeOutput pOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockSetup.FIRE_CLAY.get())
            .pattern("CC")
            .pattern("CC")
            .define('C', ItemSetup.FIRE_CLAY_BALL.get())
            .unlockedBy("has_fire_clay_ball", has(ItemSetup.FIRE_CLAY_BALL.get()))
            .save(pOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockSetup.FIRE_BRICKS.get())
            .pattern("BB")
            .pattern("BB")
            .define('B', ItemSetup.FIRE_BRICK.get())
            .unlockedBy("has_fire_brick", has(ItemSetup.FIRE_BRICK.get()))
            .save(pOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockSetup.CRUCIBLE_WINDOW.get())
            .pattern(" B ")
            .pattern("BGB")
            .pattern(" B ")
            .define('B', ItemSetup.FIRE_BRICK.get())
            .define('G', Tags.Items.GLASS_BLOCKS)
            .unlockedBy("has_fire_brick", has(ItemSetup.FIRE_BRICK.get()))
            .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
            .save(pOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockSetup.CRUCIBLE_DRAIN.get())
            .pattern("B B")
            .pattern("B B")
            .pattern("B B")
            .define('B', ItemSetup.FIRE_BRICK.get())
            .unlockedBy("has_fire_brick", has(ItemSetup.FIRE_BRICK.get()))
            .save(pOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockSetup.CASTING_BASIN.get())
            .pattern("B B")
            .pattern("B B")
            .pattern("BBB")
            .define('B', ItemSetup.FIRE_BRICK.get())
            .unlockedBy("has_fire_brick", has(ItemSetup.FIRE_BRICK.get()))
            .save(pOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockSetup.CASTING_TABLE.get())
            .pattern("BBB")
            .pattern("B B")
            .pattern("B B")
            .define('B', ItemSetup.FIRE_BRICK.get())
            .unlockedBy("has_fire_brick", has(ItemSetup.FIRE_BRICK.get()))
            .save(pOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockSetup.CRUCIBLE_TANK.get())
            .pattern("BBB")
            .pattern("BGB")
            .pattern("BBB")
            .define('B', ItemSetup.FIRE_BRICK.get())
            .define('G', Tags.Items.GLASS_BLOCKS)
            .unlockedBy("has_fire_brick", has(ItemSetup.FIRE_BRICK.get()))
            .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
            .save(pOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockSetup.CRUCIBLE_BURNER.get())
            .pattern("BBB")
            .pattern("BFB")
            .pattern("BBB")
            .define('B', ItemSetup.FIRE_BRICK.get())
            .define('F', Items.FLINT)
            .unlockedBy("has_fire_brick", has(ItemSetup.FIRE_BRICK.get()))
            .unlockedBy("has_flint", has(Items.FLINT))
            .save(pOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlockSetup.CRUCIBLE_FAWSIT.get())
            .pattern("B B")
            .pattern(" B ")
            .define('B', ItemSetup.FIRE_BRICK.get())
            .unlockedBy("has_fire_brick", has(ItemSetup.FIRE_BRICK.get()))
            .save(pOutput);
    }

    public static void shapelessRecipes(RecipeOutput pOutput) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, BlockSetup.CRUCIBLE_WINDOW.get())
            .requires(BlockSetup.FIRE_BRICKS.get())
            .requires(Tags.Items.GLASS_BLOCKS)
            .unlockedBy("has_fire_bricks", has(BlockSetup.FIRE_BRICKS.get()))
            .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS))
            .save(pOutput, ModRef.res("crucible_window_shapeless"));
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

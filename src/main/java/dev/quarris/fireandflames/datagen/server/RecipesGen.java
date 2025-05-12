package dev.quarris.fireandflames.datagen.server;

import dev.quarris.fireandflames.ModRef;
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
import net.minecraft.tags.ItemTags;
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
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.conditions.TagEmptyCondition;
import net.neoforged.neoforge.common.crafting.ConditionalRecipeOutput;
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
        metalRecipe(output, "iron", TagSetup.FluidTags.MOLTEN_IRON, Tags.Items.STORAGE_BLOCKS_RAW_IRON, Tags.Items.RAW_MATERIALS_IRON, Tags.Items.STORAGE_BLOCKS_IRON, Tags.Items.INGOTS_IRON);
        metalRecipe(output, "gold", TagSetup.FluidTags.MOLTEN_GOLD, Tags.Items.STORAGE_BLOCKS_RAW_GOLD, Tags.Items.RAW_MATERIALS_GOLD, Tags.Items.STORAGE_BLOCKS_GOLD, Tags.Items.INGOTS_GOLD);
        metalRecipe(output, "copper", TagSetup.FluidTags.MOLTEN_COPPER, Tags.Items.STORAGE_BLOCKS_RAW_COPPER, Tags.Items.RAW_MATERIALS_COPPER, Tags.Items.STORAGE_BLOCKS_COPPER, Tags.Items.INGOTS_COPPER);
    }

    private static void alloyingRecipes(RecipeOutput pOutput) {
        AlloyingRecipeBuilder.alloy(new IFluidOutput.Stack(NeoForgeMod.MILK.get(), 10))
            .requires(new FluidInput(Tags.Fluids.LAVA, 10))
            .requires(new FluidInput(Fluids.WATER, 2))
            .save(pOutput, ModRef.res("crucible/alloying/milk_from_lava_and_water"));
    }

    private static void castingRecipes(RecipeOutput pOutput) {
        CastingRecipeBuilder.table(new FluidStack(Fluids.WATER, 50), new IItemOutput.Stack(Items.BREAD))
            .withItemInput(Ingredient.of(Items.WHEAT))
            .coolingTime(40)
            .save(pOutput, ModRef.res("casting/table/bread_from_water_and_wheat"));

        CastingRecipeBuilder.table(new FluidStack(Fluids.LAVA, 100), new IItemOutput.Stack(Items.REDSTONE))
            .coolingTime(40)
            .save(pOutput, ModRef.res("casting/table/redstone_from_lava"));
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

    public static void metalRecipe(RecipeOutput output, String name, TagKey<Fluid> fluidTag, TagKey<Item> rawBlockTag, TagKey<Item> rawItemTag, TagKey<Item> blockTag, TagKey<Item> ingotTag) {
        CastingRecipeBuilder.basin(FluidIngredient.tag(fluidTag), 144 * 9, new IItemOutput.Tag(blockTag))
            .coolingTime(20 * 3 * 5)
            .saveFnf(output);

        CastingRecipeBuilder.table(FluidIngredient.tag(fluidTag), 144, new IItemOutput.Tag(ingotTag))
            .coolingTime(20 * 3)
            .saveFnf(output);

        CrucibleRecipeBuilder.smelting(fluidTag, 144 * 2, Ingredient.of(rawItemTag), 100)
            .save(output, ModRef.res("crucible/" + name + "_from_raw"));

        CrucibleRecipeBuilder.smelting(fluidTag, 144 * 2 * 9, Ingredient.of(rawBlockTag), 900)
            .heat(1100)
            .save(output, ModRef.res("crucible/" + name + "_from_raw_block"));

        CrucibleRecipeBuilder.smelting(fluidTag, 144, Ingredient.of(ingotTag), 100)
            .save(output, ModRef.res("crucible/" + name + "_from_ingot"));

        CrucibleRecipeBuilder.smelting(fluidTag, 144 * 9, Ingredient.of(blockTag), 900)
            .heat(1100)
            .save(output, ModRef.res("crucible/" + name + "_from_block"));
    }
}

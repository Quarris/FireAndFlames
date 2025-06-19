package dev.quarris.fireandflames.compat.jei;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.client.screen.ArtisanTableScreen;
import dev.quarris.fireandflames.client.screen.CrucibleScreen;
import dev.quarris.fireandflames.client.screen.TinkersWorkbenchScreen;
import dev.quarris.fireandflames.compat.CompatManager;
import dev.quarris.fireandflames.compat.IModCompat;
import dev.quarris.fireandflames.compat.jei.recipetypes.ArtisanRecipeDisplay;
import dev.quarris.fireandflames.setup.BlockSetup;
import dev.quarris.fireandflames.setup.DataMapSetup;
import dev.quarris.fireandflames.setup.RecipeSetup;
import dev.quarris.fireandflames.setup.RegistrySetup;
import dev.quarris.fireandflames.util.data.DataMapUtil;
import dev.quarris.fireandflames.world.inventory.crafting.*;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiKeyMappings;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JeiPlugin
public class JeiCompat implements IModCompat, IModPlugin {

    public JeiCompat() {
        CompatManager.JEI = this;
    }

    private IJeiKeyMappings keyMappings;

    private CrucibleRecipeCategory crucibleCategory;
    private AlloyingRecipeCategory alloyingCategory;
    private CastingRecipeCategory<BasinCastingRecipe> basinCategory;
    private CastingRecipeCategory<TableCastingRecipe> tableCategory;
    private EntityMeltingRecipeCategory entityMeltingCategory;
    private ArtisanRecipeCategory artisanCategory;

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        this.crucibleCategory = new CrucibleRecipeCategory(guiHelper);
        this.alloyingCategory = new AlloyingRecipeCategory(guiHelper);
        this.basinCategory = new CastingRecipeCategory<>(guiHelper, true, BasinCastingRecipe.class);
        this.tableCategory = new CastingRecipeCategory<>(guiHelper, false, TableCastingRecipe.class);
        this.entityMeltingCategory = new EntityMeltingRecipeCategory(guiHelper);
        this.artisanCategory = new ArtisanRecipeCategory(guiHelper);

        registration.addRecipeCategories(this.crucibleCategory);
        registration.addRecipeCategories(this.alloyingCategory);
        registration.addRecipeCategories(this.basinCategory);
        registration.addRecipeCategories(this.tableCategory);
        registration.addRecipeCategories(this.entityMeltingCategory);
        registration.addRecipeCategories(this.artisanCategory);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(BlockSetup.CRUCIBLE_CONTROLLER,
            this.crucibleCategory.getRecipeType(),
            this.alloyingCategory.getRecipeType(),
            this.basinCategory.getRecipeType(),
            this.tableCategory.getRecipeType(),
            this.entityMeltingCategory.getRecipeType()
        );

        registration.addRecipeCatalyst(BlockSetup.CASTING_BASIN, this.basinCategory.getRecipeType());
        registration.addRecipeCatalyst(BlockSetup.CASTING_TABLE, this.tableCategory.getRecipeType());

        registration.addRecipeCatalyst(BlockSetup.ARTISAN_TABLE, this.artisanCategory.getRecipeType());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(TinkersWorkbenchScreen.class, new TinkersWorkbenchGuiHandler());

        registration.addRecipeClickArea(CrucibleScreen.class, 5, 5, 50, 10, this.crucibleCategory.getRecipeType(), this.alloyingCategory.getRecipeType(), this.entityMeltingCategory.getRecipeType());
        registration.addRecipeClickArea(ArtisanTableScreen.class, 5, 5, 50, 10, this.artisanCategory.getRecipeType());
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        this.keyMappings = jeiRuntime.getKeyMappings();
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

        registerRecipesFor(registration, recipeManager, RecipeSetup.CRUCIBLE_TYPE.get(), this.crucibleCategory.getRecipeType());
        registerRecipesFor(registration, recipeManager, RecipeSetup.ALLOYING_TYPE.get(), this.alloyingCategory.getRecipeType());
        registerRecipesFor(registration, recipeManager, RecipeSetup.TABLE_CASTING_TYPE.get(), this.tableCategory.getRecipeType());
        registerRecipesFor(registration, recipeManager, RecipeSetup.BASIN_CASTING_TYPE.get(), this.basinCategory.getRecipeType());
        registerRecipesFor(registration, recipeManager, RecipeSetup.ENTITY_MELTING_TYPE.get(), this.entityMeltingCategory.getRecipeType());

        List<ArtisanRecipeDisplay> artisanRecipes = new ArrayList<>();
        recipeManager.getAllRecipesFor(RecipeSetup.ARTISAN_CRAFTING_TYPE.get()).stream().forEach(holder -> {
            ArtisanCraftingRecipe recipe = holder.value();
            artisanRecipes.add(new ArtisanRecipeDisplay(holder.id(), recipe.ingredient().ingredient(), recipe.createResult()));
        });
        recipeManager.getAllRecipesFor(RecipeSetup.MATERIAL_ARTISAN_CRAFTING_TYPE.get()).stream().forEach(holder -> {
            MaterialArtisanCraftingRecipe recipe = holder.value();
            SingleRecipeInput input = new SingleRecipeInput(new ItemStack(Items.OAK_PLANKS, 10));

            HolderLookup.Provider registries = RegistrySetup.createLookup(Minecraft.getInstance().level.registryAccess());
            //registries.lookupOrThrow(RegistrySetup.Keys.MATERIALS).getData(DataMapSetup.MATERIAL_CONVERSIONS).listElements().toList();
            for (ArtisanRecipeOutput result : recipe.createResults(input, registries)) {
                artisanRecipes.add(new ArtisanRecipeDisplay(holder.id(), Ingredient.of(input.item()), result));
            }
        });
        recipeManager.getAllRecipesFor(RecipeSetup.FAMILY_ARTISAN_CRAFTING_TYPE.get()).stream().forEach(holder -> {
            FamilyArtisanCraftingRecipe recipe = holder.value();
            for (ItemStack item : recipe.ingredient().getItems()) {
                List<ArtisanRecipeOutput> familyResults = recipe.createResults(new SingleRecipeInput(item));
                for (ArtisanRecipeOutput familyResult : familyResults) {
                    artisanRecipes.add(new ArtisanRecipeDisplay(holder.id(), Ingredient.of(item), familyResult));
                }
            }
        });
        registration.addRecipes(this.artisanCategory.getRecipeType(), artisanRecipes);
    }

    private static <I extends RecipeInput, T extends Recipe<I>> void registerRecipesFor(IRecipeRegistration registration, RecipeManager recipeManager, RecipeType<T> recipeType, mezz.jei.api.recipe.RecipeType<T> category) {
        List<T> recipes = recipeManager.getAllRecipesFor(recipeType).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(category, recipes);
    }

    public IJeiKeyMappings getKeyMappings() {
        return this.keyMappings;
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ModRef.res("jei_plugin");
    }

    @Override
    public String modId() {
        return "jei";
    }
}

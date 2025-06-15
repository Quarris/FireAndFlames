package dev.quarris.fireandflames.compat.jei;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.compat.CompatManager;
import dev.quarris.fireandflames.compat.IModCompat;
import dev.quarris.fireandflames.setup.BlockSetup;
import dev.quarris.fireandflames.setup.RecipeSetup;
import dev.quarris.fireandflames.world.crucible.crafting.BasinCastingRecipe;
import dev.quarris.fireandflames.world.crucible.crafting.TableCastingRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiKeyMappings;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class JeiCompat implements IModCompat, IModPlugin {

    public JeiCompat() {
        CompatManager.JEI = this;
    }

    private CrucibleRecipeCategory crucibleCategory;
    private AlloyingRecipeCategory alloyingCategory;
    private CastingRecipeCategory<BasinCastingRecipe> basinCategory;
    private CastingRecipeCategory<TableCastingRecipe> tableCategory;
    private EntityMeltingRecipeCategory entityMeltingCategory;
    private IJeiKeyMappings keyMappings;

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        this.crucibleCategory = new CrucibleRecipeCategory(guiHelper);
        this.alloyingCategory = new AlloyingRecipeCategory(guiHelper);
        this.basinCategory = new CastingRecipeCategory<>(guiHelper, true, BasinCastingRecipe.class);
        this.tableCategory = new CastingRecipeCategory<>(guiHelper, false, TableCastingRecipe.class);
        this.entityMeltingCategory = new EntityMeltingRecipeCategory(guiHelper);

        registration.addRecipeCategories(this.crucibleCategory);
        registration.addRecipeCategories(this.alloyingCategory);
        registration.addRecipeCategories(this.basinCategory);
        registration.addRecipeCategories(this.tableCategory);
        registration.addRecipeCategories(this.entityMeltingCategory);
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

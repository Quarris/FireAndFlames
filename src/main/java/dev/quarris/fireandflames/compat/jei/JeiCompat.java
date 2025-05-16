package dev.quarris.fireandflames.compat.jei;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.compat.CompatManager;
import dev.quarris.fireandflames.compat.IModCompat;
import dev.quarris.fireandflames.setup.BlockSetup;
import dev.quarris.fireandflames.setup.RecipeSetup;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
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
        CompatManager.jei = this;
    }

    private CrucibleRecipeCategory crucibleCategory;
    private AlloyingRecipeCategory alloyingCategory;
    private IJeiKeyMappings keyMappings;

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        this.crucibleCategory = new CrucibleRecipeCategory(registration.getJeiHelpers().getGuiHelper());
        this.alloyingCategory = new AlloyingRecipeCategory(registration.getJeiHelpers().getGuiHelper());

        registration.addRecipeCategories(this.crucibleCategory);
        registration.addRecipeCategories(this.alloyingCategory);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(BlockSetup.CRUCIBLE_CONTROLLER,
            this.crucibleCategory.getRecipeType(),
            this.alloyingCategory.getRecipeType()
        );
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

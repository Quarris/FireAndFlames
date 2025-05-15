package dev.quarris.fireandflames.compat.jei;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.compat.IModCompat;
import dev.quarris.fireandflames.setup.BlockSetup;
import dev.quarris.fireandflames.setup.RecipeSetup;
import dev.quarris.fireandflames.world.crucible.crafting.CrucibleRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class JeiCompat implements IModCompat, IModPlugin {

    private CrucibleRecipeCategory crucibleCategory;

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        this.crucibleCategory = new CrucibleRecipeCategory(registration.getJeiHelpers().getGuiHelper());

        registration.addRecipeCategories(this.crucibleCategory);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalysts(this.crucibleCategory.getRecipeType(), BlockSetup.CRUCIBLE_CONTROLLER);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        List<CrucibleRecipe> recipes = recipeManager.getAllRecipesFor(RecipeSetup.CRUCIBLE_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(this.crucibleCategory.getRecipeType(), recipes);
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

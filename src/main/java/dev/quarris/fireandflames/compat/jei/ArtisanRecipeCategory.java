package dev.quarris.fireandflames.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.compat.jei.recipetypes.JeiArtisanRecipe;
import dev.quarris.fireandflames.setup.BlockSetup;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArtisanRecipeCategory implements IRecipeCategory<JeiArtisanRecipe> {

    public static final Component TITLE = Component.translatable("gui.fireandflames.jei.category.artisan_table");
    public static final RecipeType<JeiArtisanRecipe> TYPE = new RecipeType<>(ModRef.res("artisan"), JeiArtisanRecipe.class);

    public static final int WIDTH = 96;
    public static final int HEIGHT = 32;

    private final IDrawable icon;

    public ArtisanRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemLike(BlockSetup.ARTISAN_TABLE);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, JeiArtisanRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(20, 10).addIngredients(recipe.ingredient());

        builder.addOutputSlot(60, 10);
        builder.addOutputSlot(80, 10);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, JeiArtisanRecipe recipe, IFocusGroup focuses) {
        builder.addRecipeArrow().setPosition(40, 10);
    }

    @Override
    public void draw(JeiArtisanRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        PoseStack matrix = guiGraphics.pose();
        matrix.pushPose();
        matrix.popPose();
    }

    @Override
    public void onDisplayedIngredientsUpdate(JeiArtisanRecipe recipe, List<IRecipeSlotDrawable> recipeSlots, IFocusGroup focuses) {
        recipeSlots.get(0).getDisplayedIngredient(VanillaTypes.ITEM_STACK).get();
        recipeSlots.get(1).createDisplayOverrides();
        recipeSlots.get(2).createDisplayOverrides();
        recipeSlots.get(2);

    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public RecipeType<JeiArtisanRecipe> getRecipeType() {
        return TYPE;
    }
}

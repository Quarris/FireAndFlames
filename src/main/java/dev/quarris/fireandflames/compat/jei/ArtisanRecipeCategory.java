package dev.quarris.fireandflames.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.compat.jei.recipetypes.ArtisanRecipeDisplay;
import dev.quarris.fireandflames.setup.BlockSetup;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ArtisanRecipeCategory implements IRecipeCategory<ArtisanRecipeDisplay> {

    public static final Component TITLE = Component.translatable("gui.fireandflames.jei.category.artisan_table");
    public static final RecipeType<ArtisanRecipeDisplay> TYPE = new RecipeType<>(ModRef.res("artisan"), ArtisanRecipeDisplay.class);

    public static final int WIDTH = 96;
    public static final int HEIGHT = 26;

    private final IDrawable icon;
    private final IDrawable recipeArrow;

    public ArtisanRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemLike(BlockSetup.ARTISAN_TABLE);
        this.recipeArrow = guiHelper.getRecipeArrow();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ArtisanRecipeDisplay recipe, IFocusGroup focuses) {
        builder.addInputSlot(5, 5).addItemStacks(Arrays.stream(recipe.ingredient().getItems()).map(stack -> stack.copyWithCount(recipe.output().requiredCount())).toList());

        builder.addOutputSlot(53, 5).addItemStack(recipe.output().main());
        ItemStack byproduct = recipe.output().byproduct();
        if (!byproduct.isEmpty()) {
            builder.addOutputSlot(75, 5).addItemStack(byproduct);
        }
    }

    @Override
    public void draw(ArtisanRecipeDisplay recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        PoseStack matrix = guiGraphics.pose();
        matrix.pushPose();
        matrix.popPose();
        this.recipeArrow.draw(guiGraphics, 26, 5);
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
    public RecipeType<ArtisanRecipeDisplay> getRecipeType() {
        return TYPE;
    }
}

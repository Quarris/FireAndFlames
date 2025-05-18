package dev.quarris.fireandflames.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.config.ServerConfigs;
import dev.quarris.fireandflames.setup.BlockSetup;
import dev.quarris.fireandflames.setup.ItemSetup;
import dev.quarris.fireandflames.world.crucible.crafting.CrucibleRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class CrucibleRecipeCategory implements IRecipeCategory<CrucibleRecipe> {

    public static final ResourceLocation BACKGROUND = ModRef.res("textures/gui/jei/category/crucible/recipe_background.png");
    public static final Component TITLE = Component.translatable("gui.fireandflames.jei.category.crucible");
    public static final RecipeType<CrucibleRecipe> TYPE = new RecipeType<>(ModRef.res("crucible"), CrucibleRecipe.class);

    public static final int WIDTH = 96;
    public static final int HEIGHT = 32;

    private final IDrawable icon;
    private final IDrawableStatic background;

    public CrucibleRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemLike(BlockSetup.CRUCIBLE_CONTROLLER);
        this.background = guiHelper.drawableBuilder(BACKGROUND, 0, 0, this.getWidth(), this.getHeight()).setTextureSize(this.getWidth(), this.getHeight()).build();
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, CrucibleRecipe recipe, IFocusGroup focuses) {
        builder.addAnimatedRecipeFlame(20).setPosition(34, 8);

    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CrucibleRecipe recipe, IFocusGroup focuses) {
        builder.addInputSlot(8, 8).addIngredients(recipe.ingredient());

        FluidStack fluidResult = recipe.getFluidResult();
        int fluidHeight = Mth.lerpInt((float) Math.min(1, fluidResult.getAmount() / (2 * ServerConfigs.getBlockMb())), 3, 20);
        builder.addOutputSlot(59, 6 + 20 - fluidHeight)
            .addFluidStack(fluidResult.getFluid(), fluidResult.getAmount(), fluidResult.getComponents().asPatch())
            .setFluidRenderer(fluidResult.getAmount(), false, 8, fluidHeight);

        ItemStack byproduct = recipe.byproduct();
        if (!byproduct.isEmpty()) {
            builder.addOutputSlot(72, 8).addItemStack(byproduct);
        }
    }

    @Override
    public void draw(CrucibleRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
        PoseStack matrix = guiGraphics.pose();
        matrix.pushPose();
        float scale = 0.5f;
        matrix.scale(scale, scale, 1);
        int x = (int) (42 / scale);
        int y = (int) (24 / scale);
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.literal(recipe.heat() + "°").withStyle(ChatFormatting.GRAY), x, y, 0xFFFFFF);
        matrix.popPose();
    }

    @Override
    public boolean needsRecipeBorder() {
        return false;
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
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public RecipeType<CrucibleRecipe> getRecipeType() {
        return TYPE;
    }
}

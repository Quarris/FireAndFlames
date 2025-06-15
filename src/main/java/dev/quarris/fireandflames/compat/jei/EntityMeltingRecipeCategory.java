package dev.quarris.fireandflames.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.compat.jei.widgets.DrawableEntity;
import dev.quarris.fireandflames.config.ServerConfigs;
import dev.quarris.fireandflames.setup.BlockSetup;
import dev.quarris.fireandflames.world.crucible.crafting.EntityMeltingRecipe;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class EntityMeltingRecipeCategory implements IRecipeCategory<EntityMeltingRecipe> {

    public static final ResourceLocation BACKGROUND = ModRef.res("textures/gui/jei/category/entity_melting/recipe_background.png");
    public static final ResourceLocation FLUID_SLOT_BACKGROUND = ModRef.res("textures/gui/jei/category/entity_melting/fluid_slot.png");
    public static final Component TITLE = Component.translatable("gui.fireandflames.jei.category.entity_melting");
    public static final RecipeType<EntityMeltingRecipe> TYPE = new RecipeType<>(ModRef.res("entity_melting"), EntityMeltingRecipe.class);

    public static final int WIDTH = 112;
    public static final int HEIGHT = 48;

    private final IGuiHelper guiHelper;
    private final IDrawable icon;
    private final IDrawableStatic background;
    private final IDrawableStatic fluidSlotBackground;
    private final ITickTimer rotationTimer;
    private final DrawableEntity drawableEntity;
    private final IDrawableStatic recipeFlame;
    private final IDrawableAnimated recipeArrow;

    public EntityMeltingRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.icon = guiHelper.createDrawableItemLike(BlockSetup.CRUCIBLE_CONTROLLER);
        this.background = this.guiHelper.drawableBuilder(BACKGROUND, 0, 0, this.getWidth(), this.getHeight()).setTextureSize(this.getWidth(), this.getHeight()).build();
        this.fluidSlotBackground = this.guiHelper.drawableBuilder(FLUID_SLOT_BACKGROUND, 0, 0, 10, 22).setTextureSize(10, 22).build();
        this.rotationTimer = guiHelper.createTickTimer(20, 360, false);
        this.drawableEntity = new DrawableEntity(this.guiHelper, 16, this.rotationTimer, Axis.XP.rotationDegrees(-10));

        this.recipeFlame = guiHelper.getRecipeFlameFilled();
        this.recipeArrow = guiHelper.createAnimatedRecipeArrow(20);
    }


    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EntityMeltingRecipe recipe, IFocusGroup focuses) {
        FluidStack fluidStack = recipe.result().createFluid();
        int amount = fluidStack.getAmount();
        int fluidHeight = Mth.lerpInt((float) Math.min(1, amount / ServerConfigs.getIngotMb()), 3, 20);
        builder.addOutputSlot(92, 14 + 20 - fluidHeight)
            .setBackground(this.fluidSlotBackground, -1, -1 - (20 - fluidHeight))
            .addFluidStack(fluidStack.getFluid(), fluidStack.getAmount(), fluidStack.getComponents().asPatch())
            .setFluidRenderer(fluidStack.getAmount(), false, 8, fluidHeight);
    }

    @Override
    public void draw(EntityMeltingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
        PoseStack matrix = guiGraphics.pose();
        matrix.pushPose();
        float scale = 0.5f;
        matrix.scale(scale, scale, 1);
        int x = (int) (48 / scale);
        int y = (int) (32 / scale);
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.literal(recipe.heat() + "°").withStyle(ChatFormatting.GRAY), x, y, 0xFFFFFF);
        matrix.popPose();

        this.drawableEntity.setEntities(recipe.entityPredicate().types());
        this.drawableEntity.draw(guiGraphics, 12, 16);
        this.recipeFlame.draw(guiGraphics, 41, 17);
        this.recipeArrow.draw(guiGraphics, 62, 16);
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
    public RecipeType<EntityMeltingRecipe> getRecipeType() {
        return TYPE;
    }
}

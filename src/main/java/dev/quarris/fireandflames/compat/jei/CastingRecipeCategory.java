package dev.quarris.fireandflames.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.config.ServerConfigs;
import dev.quarris.fireandflames.setup.BlockSetup;
import dev.quarris.fireandflames.world.crucible.crafting.CastingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class CastingRecipeCategory<R extends CastingRecipe> implements IRecipeCategory<R> {

    public static final ResourceLocation BACKGROUND = ModRef.res("textures/gui/jei/category/casting/recipe_background.png");
    public static final ResourceLocation ITEM_SLOT_BACKGROUND = ModRef.res("textures/gui/jei/category/casting/item_slot.png");
    public static final ResourceLocation FLUID_SLOT_BACKGROUND = ModRef.res("textures/gui/jei/category/casting/fluid_slot.png");
    public static final Component CAST_CONSUMED_TEXT = Component.translatable("gui.fireandflames.jei.cast_consumed");

    public static final int WIDTH = 128;
    public static final int HEIGHT = 64;

    private final IDrawable icon;
    private final IDrawableStatic background;
    private final IDrawable castingBlockDrawable;
    private final IDrawable itemSlotBackground;
    private final IDrawable fluidSlotBackground;

    private final Component title;
    private final RecipeType<R> type;

    public CastingRecipeCategory(IGuiHelper guiHelper, boolean isBasin, Class<R> recipeClass) {
        this.type = new RecipeType<>(ModRef.res(isBasin ? "basin_casting" : "table_casting"), recipeClass);
        this.title = Component.translatable("gui.fireandflames.jei.category." + (isBasin ? "basin" : "table") + "_casting");
        this.icon = guiHelper.createDrawableItemLike(isBasin ? BlockSetup.CASTING_BASIN : BlockSetup.CASTING_TABLE);
        this.background = guiHelper.drawableBuilder(BACKGROUND, 0, 0, this.getWidth(), this.getHeight()).setTextureSize(this.getWidth(), this.getHeight()).build();
        this.itemSlotBackground = guiHelper.drawableBuilder(ITEM_SLOT_BACKGROUND, 0, 0, 18, 18).setTextureSize(18, 18).build();
        this.fluidSlotBackground = guiHelper.drawableBuilder(FLUID_SLOT_BACKGROUND, 0, 0, 10, 22).setTextureSize(10, 22).build();
        this.castingBlockDrawable = guiHelper.createDrawableItemLike(isBasin ? BlockSetup.CASTING_BASIN : BlockSetup.CASTING_TABLE);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, R recipe, IFocusGroup focuses) {
        builder.addRecipePlusSign().setPosition(32, 16);
        builder.addAnimatedRecipeArrow(20).setPosition(70, 16);
        builder.addDrawable(this.castingBlockDrawable, 51, recipe.getItemInput().isEmpty() ? 16 : 37);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, R recipe, IFocusGroup focuses) {
        int amount = recipe.getFluidInput().amount().evaluateInt();
        int fluidHeight = Mth.lerpInt((float) Math.min(1, amount / ServerConfigs.getBlockMb()), 3, 20);
        builder.addInputSlot(18, 14 + 20 - fluidHeight)
            .addIngredients(NeoForgeTypes.FLUID_STACK, Arrays.stream(recipe.getFluidInput().ingredient().getStacks()).map(stack -> stack.copyWithAmount(amount)).toList())
            .setFluidRenderer(amount, false, 8, fluidHeight)
            .setBackground(this.fluidSlotBackground, -1, -1 - (20 - fluidHeight));

        if (!recipe.getItemInput().isEmpty()) {
            builder.addInputSlot(51, 16)
                .addIngredients(recipe.getItemInput())
                .setBackground(this.itemSlotBackground, -1, -1);
        }

        builder.addOutputSlot(96, 16)
            .addItemStack(recipe.getOutput().createItemStack())
            .setBackground(this.itemSlotBackground, -1, -1);
    }

    @Override
    public void draw(R recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
        if (!recipe.getItemInput().isEmpty() && recipe.consumesInput()) {
            PoseStack matrix = guiGraphics.pose();
            matrix.pushPose();
            float scale = 0.5f;
            matrix.scale(scale, scale, 1);
            int x = (int) (59 / scale);
            int y = (int) (8 / scale);
            guiGraphics.drawCenteredString(Minecraft.getInstance().font, CAST_CONSUMED_TEXT.copy().withStyle(ChatFormatting.RED), x, y, 0xFFFFFFFF);
            matrix.popPose();
        }
    }

    @Override
    public boolean needsRecipeBorder() {
        return false;
    }

    @Override
    public Component getTitle() {
        return this.title;
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
    public RecipeType<R> getRecipeType() {
        return this.type;
    }
}

package dev.quarris.fireandflames.compat.jei;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.compat.emi.EmiCompat;
import dev.quarris.fireandflames.compat.jei.widgets.FluidHorizontalScrollWidget;
import dev.quarris.fireandflames.setup.BlockSetup;
import dev.quarris.fireandflames.util.recipe.FluidInput;
import dev.quarris.fireandflames.util.recipe.IFluidOutput;
import dev.quarris.fireandflames.world.crucible.crafting.AlloyingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class AlloyingRecipeCategory implements IRecipeCategory<AlloyingRecipe> {

    public static final ResourceLocation BACKGROUND = ModRef.res("textures/gui/jei/category/alloying/recipe_background.png");
    public static final Component TITLE = Component.translatable("gui.fireandflames.jei.category.alloying");
    public static final RecipeType<AlloyingRecipe> TYPE = new RecipeType<>(ModRef.res("alloying"), AlloyingRecipe.class);

    public static final int WIDTH = 128;
    public static final int HEIGHT = 64;

    private final IGuiHelper guiHelper;
    private final IDrawable icon;
    private final IDrawableStatic background;

    private final IDrawableAnimated recipeArrow;

    public AlloyingRecipeCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.icon = guiHelper.createDrawableItemLike(BlockSetup.CRUCIBLE_CONTROLLER);
        this.background = this.guiHelper.drawableBuilder(BACKGROUND, 0, 0, this.getWidth(), this.getHeight()).setTextureSize(this.getWidth(), this.getHeight()).build();
        this.recipeArrow = this.guiHelper.createAnimatedRecipeArrow(20);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, AlloyingRecipe recipe, IFocusGroup focuses) {
        List<IRecipeSlotDrawable> inputSlots = builder.getRecipeSlots().getSlots(RecipeIngredientRole.INPUT);
        FluidHorizontalScrollWidget inputs = new FluidHorizontalScrollWidget(this.guiHelper, new Rect2i(7, 7, 50, 50), 4, inputSlots, false);
        builder.addSlottedWidget(inputs, inputSlots);
        builder.addInputHandler(inputs);

        List<IRecipeSlotDrawable> outputSlots = builder.getRecipeSlots().getSlots(RecipeIngredientRole.OUTPUT);
        FluidHorizontalScrollWidget outputs = new FluidHorizontalScrollWidget(this.guiHelper, new Rect2i(95, 7, 25, 50), 2, outputSlots, true);
        builder.addSlottedWidget(outputs, outputSlots);
        builder.addInputHandler(outputs);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AlloyingRecipe recipe, IFocusGroup focuses) {
        int maxFluidInput = recipe.ingredients().stream().mapToInt(input -> input.amount().evaluateInt()).max().orElse(0);
        List<FluidInput> ingredients = recipe.ingredients();

        int inputCount = EmiCompat.isLoaded() ? Math.min(ingredients.size(), 4) : ingredients.size();
        for (int i = 0; i < inputCount; i++) {
            FluidInput ingredient = ingredients.get(i);
            int amount = ingredient.amount().evaluateInt();
            int fluidHeight = Mth.lerpInt(amount / (float) maxFluidInput, 3, 28);

            IRecipeSlotBuilder slotBuilder = builder.addInputSlot()
                .setFluidRenderer(amount, false, 8, EmiCompat.isLoaded() ? 28 : fluidHeight)
                .addIngredients(NeoForgeTypes.FLUID_STACK, Arrays.stream(ingredient.ingredient().getStacks()).map(stack -> stack.copyWithAmount(amount)).toList());

            if (EmiCompat.isLoaded()) {
                slotBuilder.setPosition(10 + i * 12, 10);
            }
        }

        List<FluidStack> results = recipe.results().stream().map(IFluidOutput::createFluid).toList();
        int maxFluidOutput = results.stream().mapToInt(FluidStack::getAmount).max().orElse(0);
        int outputCount = EmiCompat.isLoaded() ? Math.min(results.size(), 2) : results.size();
        for (int i = 0; i < outputCount; i++) {
            FluidStack outputStack = results.get(i);
            int fluidHeight = Mth.lerpInt(outputStack.getAmount() / (float) maxFluidOutput, 3, 28);
            IRecipeSlotBuilder slotBuilder = builder.addOutputSlot()
                .addFluidStack(outputStack.getFluid(), outputStack.getAmount(), outputStack.getComponents().asPatch())
                .setFluidRenderer(outputStack.getAmount(), false, 8, EmiCompat.isLoaded() ? 28 : fluidHeight);

            if (EmiCompat.isLoaded()) {
                slotBuilder.setPosition(98 + i * 12, 10);
            }
        }
    }

    @Override
    public void draw(AlloyingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
        PoseStack matrix = guiGraphics.pose();
        matrix.pushPose();
        float scale = 0.5f;
        matrix.scale(scale, scale, 1);
        int x = (int) (64 / scale);
        int y = (int) (17 / scale);
        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal(recipe.heat() + "°").withStyle(ChatFormatting.GRAY), x, y, 0xFFFFFF);
        matrix.popPose();

        this.recipeArrow.draw(guiGraphics, 65, 16);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, AlloyingRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (!EmiCompat.isLoaded()) return;

        if (mouseX >= 7 && mouseX < 57 && mouseY >= 7 && mouseY < 41) {
            tooltip.add(Component.literal("Requires all of:"));
            recipe.ingredients().stream().forEach(input -> {
                FluidIngredient ingredient = input.ingredient();
                FluidStack[] stacks = ingredient.getStacks();
                long gameTime = Minecraft.getInstance().level.getGameTime();
                int targetStack = (int) ((gameTime / 20) % stacks.length);
                FluidStack stack = stacks[targetStack].copyWithAmount(input.amount().evaluateInt());
                tooltip.add(stack.getHoverName().plainCopy().append(" " + stack.getAmount() + " mb").withStyle(ChatFormatting.GRAY));
            });
        }

        if (mouseX >= 95 && mouseX < 121 && mouseY >= 7 && mouseY < 41) {
            tooltip.add(Component.literal("Outputs:"));
            recipe.results().stream().map(IFluidOutput::createFluid).forEach(stack -> tooltip.add(stack.getHoverName().plainCopy().append(" " + stack.getAmount() + " mb").withStyle(ChatFormatting.GRAY)));
        }
    }

    @Override
    public boolean handleInput(AlloyingRecipe recipe, double mouseX, double mouseY, InputConstants.Key input) {
        return IRecipeCategory.super.handleInput(recipe, mouseX, mouseY, input);
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
    public RecipeType<AlloyingRecipe> getRecipeType() {
        return TYPE;
    }
}

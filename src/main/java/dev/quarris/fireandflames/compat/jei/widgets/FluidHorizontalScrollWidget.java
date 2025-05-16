package dev.quarris.fireandflames.compat.jei.widgets;

import com.mojang.blaze3d.platform.InputConstants;
import dev.quarris.fireandflames.ModRef;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.api.gui.inputs.RecipeSlotUnderMouse;
import mezz.jei.api.gui.widgets.IScrollGridWidget;
import mezz.jei.api.gui.widgets.ISlottedRecipeWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.Optional;

public class FluidHorizontalScrollWidget implements IScrollGridWidget, ISlottedRecipeWidget, IJeiInputHandler {
    private static final int SCROLLBAR_PADDING = 2;
    private static final int SCROLLBAR_HEIGHT = 7;
    private static final int MIN_SCROLL_MARKER_WIDTH = 10;

    public static int getScrollBoxScrollbarExtraHeight() {
        return SCROLLBAR_HEIGHT + SCROLLBAR_PADDING;
    }

    protected static Rect2i calculateScrollArea(int width, int height) {
        return new Rect2i(
            1,
            height - SCROLLBAR_HEIGHT - 3,
            width - 2,
            SCROLLBAR_HEIGHT
        );
    }

    protected Rect2i area;

    private final Rect2i scrollArea;
    private final IDrawable scrollbarMarker;
    private final IDrawable scrollbarBackground;
    /**
     * Position of the mouse on the scroll marker when dragging.
     */
    private double dragOriginX = -1;
    /**
     * Amount scrolled in percent, (0 = top, 1 = bottom)
     */
    private float scrollOffset = 0;

    private final int visibleTanks;
    private final int hiddenColumns;
    private final List<IRecipeSlotDrawable> slots;

    public FluidHorizontalScrollWidget(IGuiHelper guiHelper, Rect2i area, int visibleTanks, List<IRecipeSlotDrawable> slots, boolean output) {
        this.area = area;
        this.scrollArea = calculateScrollArea(area.getWidth(), area.getHeight());
        this.scrollbarMarker = output ?
            guiHelper.drawableBuilder(ModRef.res("textures/gui/jei/category/alloying/scrollbar_marker_output.png"), 0, 0, 7, 7).setTextureSize(7, 7).build() :
            guiHelper.drawableBuilder(ModRef.res("textures/gui/jei/category/alloying/scrollbar_marker.png"), 0, 0, 11, 7).setTextureSize(11, 7).build();
        this.scrollbarBackground = output ?
            guiHelper.drawableBuilder(ModRef.res("textures/gui/jei/category/alloying/scrollbar_output.png"), 0, 0, 24, 9).setTextureSize(24, 9).build() :
            guiHelper.drawableBuilder(ModRef.res("textures/gui/jei/category/alloying/scrollbar.png"), 0, 0, 48, 9).setTextureSize(48, 9).build();

        this.slots = slots;
        this.visibleTanks = visibleTanks;
        this.hiddenColumns = Math.max(slots.size() - visibleTanks, 0);
    }

    protected Rect2i calculateScrollbarMarkerArea() {
        int totalSpace = scrollArea.getWidth() - SCROLLBAR_PADDING;
        int scrollMarkerWidth = this.scrollbarMarker.getWidth();
        int scrollMarkerHeight = scrollArea.getHeight() - SCROLLBAR_PADDING;
        int scrollbarMarkerX = Math.round((totalSpace - scrollMarkerWidth) * scrollOffset);
        return new Rect2i(
            scrollArea.getX() + SCROLLBAR_PADDING / 2 + scrollbarMarkerX,
            scrollArea.getY() + SCROLLBAR_PADDING / 2,
            scrollMarkerWidth,
            scrollMarkerHeight
        );
    }

    protected int getVisibleAmount() {
        return visibleTanks;
    }

    protected int getHiddenAmount() {
        return hiddenColumns;
    }

    protected void drawContents(GuiGraphics guiGraphics, double mouseX, double mouseY, float scrollOffsetY) {
        final int totalSlots = slots.size();
        final int firstRow = getTankIndexForScroll(hiddenColumns, getScrollOffset());
        int maxFluidHeight = this.slots.stream().mapToInt(slot -> slot.getDisplayedIngredient(NeoForgeTypes.FLUID_STACK).orElse(FluidStack.EMPTY).getAmount()).max().orElse(0);
        final int slotWidth = 8;

        final int y = 2;
        for (int tank = 0; tank < visibleTanks; tank++) {
            final int x = 2 + tank * (slotWidth + 4);
            final int slotIndex = firstRow + tank;
            if (slotIndex < totalSlots) {
                IRecipeSlotDrawable slot = slots.get(slotIndex);
                int amount = slot.getDisplayedIngredient(NeoForgeTypes.FLUID_STACK).orElse(FluidStack.EMPTY).getAmount();
                int fluidHeight = Mth.lerpInt(amount / (float) maxFluidHeight, 3, 28);
                slot.setPosition(x + 1, y + 1 + 28 - fluidHeight);
                slot.draw(guiGraphics);
            }
        }
    }

    protected float getScrollOffset() {
        return scrollOffset;
    }

    @Override
    public final ScreenRectangle getArea() {
        return new ScreenRectangle(this.area.getX(), this.area.getY(), this.area.getWidth(), this.area.getHeight());
    }

    @Override
    public final ScreenPosition getPosition() {
        return new ScreenPosition(this.area.getX(), this.area.getY());
    }

    @Override
    public final void drawWidget(GuiGraphics guiGraphics, double mouseX, double mouseY) {
        scrollbarBackground.draw(guiGraphics, scrollArea.getX(), scrollArea.getY());
        Rect2i scrollbarMarkerArea = calculateScrollbarMarkerArea();
        scrollbarMarker.draw(guiGraphics, scrollbarMarkerArea.getX(), scrollbarMarkerArea.getY());

        drawContents(guiGraphics, mouseX, mouseY, scrollOffset);
    }

    @Override
    public final boolean handleInput(double mouseX, double mouseY, IJeiUserInput userInput) {
        if (!userInput.getKey().equals(InputConstants.Type.MOUSE.getOrCreate(InputConstants.MOUSE_BUTTON_LEFT))) {
            return false;
        }

        if (!userInput.isSimulate()) {
            dragOriginX = -1;
        }

        if (scrollArea.contains((int) mouseX, (int) mouseY)) {
            if (getHiddenAmount() == 0) {
                return false;
            }

            if (userInput.isSimulate()) {
                Rect2i scrollMarkerArea = calculateScrollbarMarkerArea();
                if (!scrollMarkerArea.contains((int) mouseX, (int) mouseY)) {
                    moveScrollbarCenterTo(scrollMarkerArea, mouseX);
                    scrollMarkerArea = calculateScrollbarMarkerArea();
                }
                dragOriginX = mouseX - scrollMarkerArea.getX();
            }
            return true;
        }
        return false;
    }

    @Override
    public final boolean handleMouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
        if (getHiddenAmount() > 0) {
            scrollOffset -= calculateScrollAmount(scrollDeltaY);
            scrollOffset = Mth.clamp(scrollOffset, 0.0F, 1.0F);
        } else {
            scrollOffset = 0.0f;
        }
        return true;
    }

    @Override
    public final boolean handleMouseDragged(double mouseX, double mouseY, InputConstants.Key mouseKey, double dragX, double dragY) {
        if (dragOriginX < 0 || mouseKey.getValue() != InputConstants.MOUSE_BUTTON_LEFT) {
            return false;
        }

        Rect2i scrollbarMarkerArea = calculateScrollbarMarkerArea();

        double leftX = mouseX - dragOriginX;
        moveScrollbarTo(scrollbarMarkerArea, leftX);
        return true;
    }

    private void moveScrollbarCenterTo(Rect2i scrollMarkerArea, double centerX) {
        double leftX = centerX - (scrollMarkerArea.getWidth() / 2.0);
        moveScrollbarTo(scrollMarkerArea, leftX);
    }

    private void moveScrollbarTo(Rect2i scrollMarkerArea, double leftX) {
        int minX = scrollArea.getX();
        int maxX = scrollArea.getX() + scrollArea.getWidth() - scrollMarkerArea.getWidth();
        double relativeX = leftX - minX;
        int totalSpace = maxX - minX;
        scrollOffset = (float) (relativeX / (float) totalSpace);
        scrollOffset = Mth.clamp(scrollOffset, 0.0F, 1.0F);
    }

    protected float calculateScrollAmount(double scrollDeltaY) {
        int hiddenRows = getHiddenAmount();
        return (float) (scrollDeltaY / (double) hiddenRows);
    }

    @Override
    public ScreenRectangle getScreenRectangle() {
        return this.getArea();
    }

    @Override
    public IScrollGridWidget setPosition(int xPos, int yPos) {
        this.area.setPosition(xPos, yPos);
        return this;
    }

    @Override
    public int getWidth() {
        return this.area.getWidth();
    }

    @Override
    public int getHeight() {
        return this.area.getHeight();
    }

    @Override
    public Optional<RecipeSlotUnderMouse> getSlotUnderMouse(double mouseX, double mouseY) {
        final int firstRow = getTankIndexForScroll(hiddenColumns, getScrollOffset());
        final int endIndex = Math.min(firstRow + (visibleTanks), slots.size());
        for (int i = firstRow; i < endIndex; i++) {
            IRecipeSlotDrawable slot = slots.get(i);
            if (slot.isMouseOver(mouseX, mouseY)) {
                return Optional.of(new RecipeSlotUnderMouse(slot, getPosition()));
            }
        }
        return Optional.empty();
    }

    private int getTankIndexForScroll(int hiddenRows, float scrollOffset) {
        int index = (int) ((double) (scrollOffset * (float) hiddenRows) + 0.5D);
        return Math.max(index, 0);
    }
}

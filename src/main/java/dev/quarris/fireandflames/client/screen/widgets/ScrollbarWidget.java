package dev.quarris.fireandflames.client.screen.widgets;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.util.Mth;

public class ScrollbarWidget extends AbstractWidget {

    private final WidgetSprites scrollbarSprites;
    private final WidgetSprites markerSprite;
    private final OnScroll onScroll;

    private int maxScroll;
    private float scrolled;
    private int scrollOption;

    public ScrollbarWidget(WidgetSprites scrollbarSprites, WidgetSprites markerSprite, int x, int y, int width, int height, OnScroll onScroll) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.scrollbarSprites = scrollbarSprites;
        this.markerSprite = markerSprite;
        this.onScroll = onScroll;
    }

    public void updateMaxScroll(int maxScroll) {
        this.maxScroll = Math.max(maxScroll, 0);
        this.setScroll(0);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        //if (!this.canScroll()) return;
        int scrollMarkerHeight = Math.max(3, Math.min(this.getHeight() - 2, 3 + this.getHeight() / (this.maxScroll + 1)));
        this.renderSprite(guiGraphics, this.scrollbarSprites, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        this.renderSprite(guiGraphics, this.markerSprite, this.getX() + 1, this.getY() + 1 + (int) Math.floor(this.scrolled * (this.getHeight() - scrollMarkerHeight - 2)), this.getWidth() - 2, scrollMarkerHeight);
    }

    public boolean canScroll() {
        return this.maxScroll > 0;
    }

    private void renderSprite(GuiGraphics guiGraphics, WidgetSprites sprite, int x, int y, int width, int height) {
        guiGraphics.blitSprite(sprite.get(this.isActive(), this.isFocused()), x, y, width, height);
    }

    private void setScroll(float scroll) {
        this.scrolled = Mth.clamp(scroll, 0, 1);
        int newOption = (int) (this.scrolled * (this.maxScroll + 0.5));
        if (this.scrollOption != newOption) {
            this.scrollOption = newOption;
            this.onScroll.select(newOption);
        }
    }

    private void scroll(float scroll) {
        this.setScroll(this.scrolled + scroll * this.getScrollStep());
    }

    private float getScrollStep() {
        if (!this.canScroll()) return 0;
        return 1f / (this.maxScroll);
    }

    private void setScrollFromMousePosition(double mouseY) {
        this.setScroll((float) ((mouseY - (this.getY() + 1)) / (this.getHeight() - 2)));
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        this.setScrollFromMousePosition(mouseY);
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        this.setScrollFromMousePosition(mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.scroll((float) -scrollY);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == InputConstants.KEY_UP) {
            this.scroll(-1);
            return true;
        }

        if (keyCode == InputConstants.KEY_DOWN) {
            this.scroll(1);
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public interface OnScroll {
        void select(int option);
    }
}

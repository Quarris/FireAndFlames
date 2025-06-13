package dev.quarris.fireandflames.client.screen.widgets;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.ResourceLocation;

public class HeldButton extends ImageButton {

    private final ResourceLocation heldSprite;
    private final OnRelease onRelease;
    private boolean isHeld;

    public HeldButton(int x, int y, int width, int height, WidgetSprites sprites, ResourceLocation heldSprite, OnPress onPress, OnRelease onRelease) {
        super(x, y, width, height, sprites, onPress);
        this.heldSprite = heldSprite;
        this.onRelease = onRelease;
    }

    public void forceStop(boolean triggerRelease) {
        this.isHeld = false;
        if (triggerRelease) {
            this.onRelease.onRelease(this);
        }
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.isHeld) {
            guiGraphics.blitSprite(this.heldSprite, this.getX(), this.getY(), this.getWidth(), this.getHeight());
            return;
        }

        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            this.isHeld = true;
            return true;
        }

        return false;
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        if (this.isHeld) {
            this.onRelease.onRelease(this);
            this.isHeld = false;
        }
    }

    public interface OnRelease {
        void onRelease(Button button);
    }
}

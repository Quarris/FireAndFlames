package dev.quarris.fireandflames.compat.jade;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import snownee.jade.api.ui.Color;
import snownee.jade.api.ui.ProgressStyle;
import snownee.jade.overlay.DisplayHelper;

public class HeatProgressStyle extends ProgressStyle {

    private int startColor;
    private int endColor;

    @Override
    public ProgressStyle color(int startColor, int endColor) {
        this.startColor = startColor;
        this.endColor = endColor;
        return this;
    }

    @Override
    public ProgressStyle textColor(int i) {
        return this;
    }

    @Override
    public void render(GuiGraphics guiGraphics, float x, float y, float w, float h, float progress, Component component) {
        if (progress < 0.1F) {
            DisplayHelper.INSTANCE.drawGradientRect(guiGraphics, x, y, w * progress, h, this.startColor, this.endColor, true);
        } else {
            Color endColor = Color.rgb(this.endColor);
            Color highlight = Color.hsl(endColor.getHue(), endColor.getSaturation(), Math.min(endColor.getLightness() + 0.2, 1.0F), endColor.getOpacity());
            float hlWidth = w * 0.1F;
            float normalWidth = w * progress - hlWidth;
            DisplayHelper.INSTANCE.drawGradientRect(guiGraphics, x, y, normalWidth, h, this.startColor, this.endColor, true);
            DisplayHelper.INSTANCE.drawGradientRect(guiGraphics, x + normalWidth, y, hlWidth, h, this.endColor, highlight.toInt(), true);
        }
    }
}

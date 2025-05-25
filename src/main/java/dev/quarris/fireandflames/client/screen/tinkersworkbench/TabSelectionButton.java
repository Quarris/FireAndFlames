package dev.quarris.fireandflames.client.screen.tinkersworkbench;

import dev.quarris.fireandflames.ModRef;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class TabSelectionButton extends AbstractButton {

    public static final ResourceLocation SLOT_SPRITE = ModRef.res("container/tinkers_workbench/tool_tab_button");

    private final ResourceLocation id;
    private final TabIconRenderer tabIconRenderer;
    private final TabSelectionWidget.OnToolTypeSelected onPress;

    public TabSelectionButton(ResourceLocation id, int x, int y, int width, int height, TabIconRenderer tabIconRenderer, TabSelectionWidget.OnToolTypeSelected onPress) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.id = id;
        this.tabIconRenderer = tabIconRenderer;
        this.onPress = onPress;
    }

    @Override
    public void onPress() {
        this.onPress.select(this.id);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(SLOT_SPRITE, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        this.tabIconRenderer.renderTabIcon(guiGraphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), mouseX, mouseY, partialTick);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }

    protected void defaultButtonNarrationText(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.focused"));
            } else {
                narrationElementOutput.add(NarratedElementType.USAGE, Component.translatable("narration.button.usage.hovered"));
            }
        }
    }

    public interface TabIconRenderer {
        void renderTabIcon(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick);
    }

    public record StackTabRenderer(ItemStack stack) implements TabIconRenderer {
        @Override
        public void renderTabIcon(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
            graphics.renderFakeItem(stack, x + (width - 16) / 2, y + (height - 16) / 2);
        }
    }
}

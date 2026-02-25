package dev.quarris.fireandflames.client.screen;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.client.screen.widgets.DefaultedEditBox;
import dev.quarris.fireandflames.client.screen.widgets.TabSelectionWidget;
import dev.quarris.fireandflames.network.payload.SBTinkersWorkbenchChangeTab;
import dev.quarris.fireandflames.network.payload.SBTinkersWorkbenchToolNameChange;
import dev.quarris.fireandflames.world.inventory.menu.NamedSlot;
import dev.quarris.fireandflames.world.inventory.menu.TinkersWorkbenchMenu;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Locale;

public class TinkersWorkbenchScreen extends AbstractContainerScreen<TinkersWorkbenchMenu> {

    private static final ResourceLocation SLOT_SPRITE = ModRef.res("container/tinkers_workbench/slot");
    private static final ResourceLocation BG_LOCATION = ModRef.res("textures/gui/container/tinkers_workbench.png");

    public TabSelectionWidget tabSelection;
    public DefaultedEditBox toolNameEditor;

    public TinkersWorkbenchScreen(TinkersWorkbenchMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        this.imageWidth = 176;
        this.imageHeight = 219;
        this.inventoryLabelY = this.imageHeight - 94;
        super.init();

        this.tabSelection = this.addRenderableWidget(new TabSelectionWidget(this.leftPos - 43, this.topPos + 20, 43, 127, this::changeTab));
        this.toolNameEditor = this.addRenderableWidget(new DefaultedEditBox(this.font, this.leftPos + 33, this.topPos + 20, 110, 16, Component.literal("Tool Name Edit Box")));
        this.toolNameEditor.setResponder(this::onNameChanged);

        this.menu.setNameChangedListener(name -> {
            this.toolNameEditor.setDefaultValue(name);
            if (StringUtil.isBlank(name)) {
                this.toolNameEditor.setValue("");
            }
        });
    }

    private void changeTab(ResourceLocation tabName) {
        if (this.menu.setTabByName(tabName)) {
            PacketDistributor.sendToServer(new SBTinkersWorkbenchChangeTab(tabName));
        }
    }

    private void onNameChanged(String name) {
        if (this.menu.setToolName(name)) {
            PacketDistributor.sendToServer(new SBTinkersWorkbenchToolNameChange(name));
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BG_LOCATION, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderSlot(GuiGraphics guiGraphics, Slot slot) {
        if (slot instanceof NamedSlot) {
            guiGraphics.blitSprite(SLOT_SPRITE, slot.x - 1, slot.y - 1, 18, 18);
        }
        super.renderSlot(guiGraphics, slot);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        if (this.getSlotUnderMouse() instanceof NamedSlot namedSlot && !namedSlot.hasItem()) {
            String key = Util.makeDescriptionId("tool_part.slot", ModRef.res(namedSlot.getName().toLowerCase(Locale.ROOT)));
            guiGraphics.renderTooltip(this.font, Language.getInstance().has(key) ? Component.translatable(key): Component.literal(namedSlot.getName()), x, y);
            return;
        }

        super.renderTooltip(guiGraphics, x, y);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        if (this.toolNameEditor.canConsumeInput()) {
            return this.toolNameEditor.keyPressed(keyCode, scanCode, modifiers);
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.getFocused() != null && this.isDragging() && button == 0) {
            return this.getFocused().mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.setFocused(null);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop, int mouseButton) {
        if (!super.hasClickedOutside(mouseX, mouseY, guiLeft, guiTop, mouseButton)) {
            return false;
        }

        if (this.tabSelection.getChildAt(((int) mouseX), ((int) mouseY)).isPresent()) {
            return false;
        }

        // TODO Add check for stats sidebar

        return true;
    }
}

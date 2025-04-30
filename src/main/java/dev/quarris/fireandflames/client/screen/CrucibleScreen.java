package dev.quarris.fireandflames.client.screen;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.client.screen.components.CrucibleFluidTankComponent;
import dev.quarris.fireandflames.world.block.entity.CrucibleControllerBlockEntity;
import dev.quarris.fireandflames.world.inventory.menu.CrucibleMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrucibleScreen extends EffectRenderingInventoryScreen<CrucibleMenu> {

    private static final ResourceLocation CRUCIBLE_LOCATION = ModRef.res("textures/gui/container/crucible.png");
    private static final ResourceLocation SLOT_SPRITE = ModRef.res("container/crucible/slot");
    private static final ResourceLocation SCROLLER_SPRITE = ModRef.res("container/crucible/scroller");
    private static final ResourceLocation SCROLLER_DISABLED_SPRITE = ModRef.res("container/crucible/scroller_disabled");

    private CrucibleFluidTankComponent fluidTankComponent;

    public CrucibleScreen(CrucibleMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageWidth = 176;
        this.imageHeight = 209;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        this.fluidTankComponent = new CrucibleFluidTankComponent(menu.crucible::getFluidTank, this.leftPos + 8, this.topPos + 18, 48, 79);
    }

    @Override
    protected void containerTick() {
    }

    @Override
    public void render(GuiGraphics pGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGraphics, pMouseX, pMouseY, pPartialTick);
        CrucibleControllerBlockEntity crucible = this.getMenu().crucible;
        this.fluidTankComponent.render(pGraphics, crucible.getLevel(), crucible.getBlockPos(), pMouseX, pMouseY, pPartialTick);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        pGuiGraphics.blit(CRUCIBLE_LOCATION, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        for (int slotId = 0; slotId < this.getMenu().crucible.getInventory().getSlots(); slotId++) {
            Slot slot = this.getMenu().getSlot(slotId);
            if (!slot.isActive()) continue;
            pGuiGraphics.blitSprite(SLOT_SPRITE, this.leftPos + slot.x - 1, this.topPos + slot.y - 1 - this.menu.getScroll() * 18, 18, 18);
        }

        ResourceLocation scrollerSprite = this.getMenu().canScroll() ? SCROLLER_SPRITE : SCROLLER_DISABLED_SPRITE;
        int scrollPos = (int) (this.getMenu().getScrollBarPosition() * (90 - 15)); // ... * (scrollBarHeight - scrollHeight)

        pGuiGraphics.blitSprite(scrollerSprite, this.leftPos + 156, this.topPos + 18 + scrollPos, 12, 15);
        /*
        SpriteContents contents = sprite.contents();
        int drawX, drawY;
        int drawWidth = (tankWidth / contents.width()) * contents.width();
        int drawHeight = (tankHeight / contents.height()) * contents.height();
        for (drawX = 0; drawX < drawWidth; drawX += contents.width()) {
            for (drawY = 0; drawY < drawHeight; drawY += contents.height()) {
                pGuiGraphics.blit(tankPosX + drawX, tankPosY + drawY, 0, contents.width(), contents.height(), sprite, red, green, blue, 1.0f);
            }
        }

        if (drawWidth < tankWidth) {
            for (drawY = 0; drawY < drawHeight; drawY += contents.height()) {
                pGuiGraphics.blit(tankPosX + drawWidth, tankPosY + drawY, 0, tankWidth - drawWidth, contents.height(), sprite, red, green, blue, 1.0f);
            }
        }

        if (drawHeight < tankHeight) {
            for (drawX = 0; drawX < drawWidth; drawX += contents.width()) {
                pGuiGraphics.blit(tankPosX + drawX, tankPosY + drawHeight, 0, contents.width(), tankHeight - drawHeight, sprite, red, green, blue, 1.0f);
            }
        }

        if (drawHeight < tankHeight && drawWidth < tankWidth) {
            pGuiGraphics.blit(tankPosX + drawWidth, tankPosY + drawHeight, 0, tankWidth - drawWidth, tankHeight - drawHeight, sprite, red, green, blue, 1.0f);
        }
        */
    }

    @Override
    protected void renderSlotHighlight(GuiGraphics pGraphics, Slot pSlot, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.isPlayerSlot(pSlot)) {
            super.renderSlotHighlight(pGraphics, pSlot, pMouseX, pMouseY, pPartialTick);
            return;
        }

        if (pSlot.isHighlightable()) {
            renderSlotHighlight(pGraphics, pSlot.x, pSlot.y - this.menu.getScroll() * 18, 0, getSlotColor(pSlot.index));
        }
    }

    @Override
    protected void renderSlotContents(GuiGraphics pGraphics, ItemStack pStack, Slot pSlot, @Nullable String pCountString) {
        if (this.isPlayerSlot(pSlot)) {
            super.renderSlotContents(pGraphics, pStack, pSlot, pCountString);
            return;
        }

        int drawX = pSlot.x;
        int drawY = pSlot.y - this.menu.getScroll() * 18;
        int seed = drawX + drawY * this.imageWidth;
        if (pSlot.isFake()) {
            pGraphics.renderFakeItem(pStack, drawX, drawY, seed);
        } else {
            pGraphics.renderItem(pStack, drawX, drawY, seed);
        }

        pGraphics.renderItemDecorations(this.font, pStack, drawX, drawY, pCountString);

        var progress = getMenu().dataAccess.get(pSlot.index);
        if (progress > 0) {
            int alpha = Mth.lerpDiscrete(progress / 100f, 50, 150);
            int red = Mth.lerpDiscrete(progress / 100f, 254, 254);
            int green = Mth.lerpDiscrete(progress / 100f, 195, 75);
            int drawHeight = Mth.lerpDiscrete(progress / 100f, 0, 16);
            int color = alpha << 24 | red << 16 | green << 8;
            pGraphics.fill(drawX, drawY + 16 - drawHeight, drawX + 16, drawY + 16, 200, color);
        }

    }

    private boolean isPlayerSlot(Slot pSlot) {
        return pSlot.index >= this.getMenu().crucible.getInventory().getSlots();
    }

    @Override
    protected boolean isHovering(@NotNull Slot pSlot, double pMouseX, double pMouseY) {
        if (this.isPlayerSlot(pSlot)) {
            return super.isHovering(pSlot, pMouseX, pMouseY);
        }

        return this.isHovering(pSlot.x, pSlot.y - this.menu.getScroll() * 18, 16, 16, pMouseX, pMouseY);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return super.isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.getMenu().scroll((int) scrollY);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.fluidTankComponent.isHovering(mouseX, mouseY)) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        int clickedFluidSlot = this.fluidTankComponent.getHoveringFluidTank(mouseX, mouseY);
        if (clickedFluidSlot < 0 || !this.menu.clickMenuButton(this.minecraft.player, clickedFluidSlot)) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, clickedFluidSlot);
        return true;

    }
}

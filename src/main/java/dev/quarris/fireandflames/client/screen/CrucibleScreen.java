package dev.quarris.fireandflames.client.screen;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.world.block.entity.CrucibleControllerBlockEntity;
import dev.quarris.fireandflames.world.inventory.menu.CrucibleMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.textures.FluidSpriteCache;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrucibleScreen extends EffectRenderingInventoryScreen<CrucibleMenu> {

    private static final ResourceLocation CRUCIBLE_LOCATION = ModRef.res("textures/gui/container/crucible.png");
    private static final ResourceLocation SLOT_SPRITE = ModRef.res("container/crucible/slot");
    private static final ResourceLocation SCROLLER_SPRITE = ModRef.res("container/crucible/scroller");
    private static final ResourceLocation SCROLLER_DISABLED_SPRITE = ModRef.res("container/crucible/scroller_disabled");

    public CrucibleScreen(CrucibleMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.imageWidth = 176;
        this.imageHeight = 209;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void containerTick() {
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
        if (pMouseX >= this.leftPos + 7 && pMouseX < this.leftPos + 57 && pMouseY > this.topPos + 17 && pMouseY < this.topPos + 98) {
            this.setTooltipForNextRenderPass(Component.literal(this.menu.crucible.getFluidTank().getFluid().toString()));
        }
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

        CrucibleControllerBlockEntity crucible = this.getMenu().crucible;
        FluidTank fluidTank = this.menu.crucible.getFluidTank();
        FluidStack fluid = fluidTank.getFluid();
        int tankPosX = this.leftPos + 8;
        int tankPosY = this.topPos + 18;
        int tankWidth = 48;
        int tankHeight = 79;

        TextureAtlasSprite sprite = FluidSpriteCache.getFluidSprites(crucible.getLevel(), crucible.getBlockPos(), fluid.getFluid().defaultFluidState())[0];
        IClientFluidTypeExtensions fluidExtensions = IClientFluidTypeExtensions.of(fluid.getFluidType());
        int color = fluidExtensions.getTintColor(fluid.getFluid().defaultFluidState(), crucible.getLevel(), crucible.getBlockPos());

        float red = (float) (color >> 16 & 0xFF) / 255.0F;
        float green = (float) (color >> 8 & 0xFF) / 255.0F;
        float blue = (float) (color & 0xFF) / 255.0F;

        pGuiGraphics.blit(tankPosX, tankPosY, 0, tankWidth, tankHeight, sprite, red, green, blue, 1.0f);

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
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        this.getMenu().scroll((int) scrollY);
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

}

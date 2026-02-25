package dev.quarris.fireandflames.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.network.payload.CBArtisanTableSetOutputSelection;
import dev.quarris.fireandflames.world.inventory.crafting.ArtisanRecipeOutput;
import dev.quarris.fireandflames.world.inventory.menu.ArtisanTableMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArtisanTableScreen extends AbstractContainerScreen<ArtisanTableMenu> {

    private static final ResourceLocation BACKGROUND = ModRef.res("textures/gui/container/artisan_table.png");
    private static final ResourceLocation SLOT_SPRITE = ModRef.res("container/slot");

    private static final WidgetSprites LEFT_ARROW_SPITES = new WidgetSprites(ModRef.res("container/left_arrow"), ModRef.res("container/left_arrow_disabled"), ModRef.res("container/left_arrow_highlighted"));
    private static final WidgetSprites RIGHT_ARROW_SPITES = new WidgetSprites(ModRef.res("container/right_arrow"), ModRef.res("container/right_arrow_disabled"), ModRef.res("container/right_arrow_highlighted"));
    private ImageButton leftArrow;
    private ImageButton rightArrow;

    public ArtisanTableScreen(ArtisanTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        this.imageWidth = 176;
        this.imageHeight = 184;
        this.inventoryLabelY = this.imageHeight - 94;
        super.init();

        this.leftArrow = this.addRenderableWidget(new ImageButton(this.leftPos + 60, this.topPos + 65, 16, 11, LEFT_ARROW_SPITES, b -> this.select(this.getMenu().getOutputSelection() - 1), Component.literal("Select Left")));
        this.rightArrow = this.addRenderableWidget(new ImageButton(this.leftPos + 100, this.topPos + 65, 16, 11, RIGHT_ARROW_SPITES, b -> this.select(this.getMenu().getOutputSelection() + 1), Component.literal("Select Right")));
        this.leftArrow.visible = false;
        this.rightArrow.visible = false;
    }

    @Override
    protected void containerTick() {
        this.leftArrow.visible = !this.getMenu().getOutputs().isEmpty();
        this.rightArrow.visible = !this.getMenu().getOutputs().isEmpty();
        if (!this.leftArrow.isHovered()) this.leftArrow.setFocused(false);
        if (!this.rightArrow.isHovered()) this.rightArrow.setFocused(false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        this.renderHoveredOutput(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        List<ArtisanRecipeOutput> outputs = getMenu().getOutputs();

        if (!outputs.isEmpty()) {
            PoseStack matrix = guiGraphics.pose();
            int recipeCount = 3;
            for (int i = -recipeCount; i <= recipeCount; i++) {
                int offset = Mth.abs(i);
                int outputIndex = (i + outputs.size() + this.getMenu().getOutputSelection()) % outputs.size();
                ArtisanRecipeOutput output = outputs.get(outputIndex);
                int slotX = this.leftPos + this.imageWidth / 2 + i * 10;
                int slotY = this.topPos + 45 + 9;
                matrix.pushPose();
                float scale = 1 - offset * 0.1f;
                matrix.translate(slotX, slotY, -offset * 20);
                matrix.scale(scale, scale, scale);
                guiGraphics.blitSprite(SLOT_SPRITE, -9, -9, 150, 18, 18);
                guiGraphics.renderItem(output.main(), -8, -8, 0, 20);
                matrix.popPose();
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
        if (!this.getMenu().getOutputs().isEmpty()) {
            PoseStack pose = guiGraphics.pose();
            pose.pushPose();
            ArtisanRecipeOutput output = this.getMenu().getOutputs().get(this.getMenu().getOutputSelection());
            guiGraphics.drawString(this.font, "-" + output.requiredCount(), 62, 33, 0xfffa342d);
            pose.popPose();
        }
    }

    public void renderHoveredOutput(GuiGraphics guiGraphics, int mouseX, int mouseY) {

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!this.getMenu().getOutputs().isEmpty() &&
            mouseX >= this.leftPos + 56 && mouseX < this.leftPos + 56 + 64 &&
            mouseY >= this.topPos + 48 && mouseY < this.topPos + 48 + 36) {
            int selection = (int) (this.getMenu().getOutputSelection() - (scrollX + scrollY));
            this.select(selection);
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private void select(int selection) {
        this.getMenu().selectOutput(selection);
        PacketDistributor.sendToServer(new CBArtisanTableSetOutputSelection(selection));
    }
}

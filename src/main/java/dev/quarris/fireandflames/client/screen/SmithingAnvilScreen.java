package dev.quarris.fireandflames.client.screen;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.client.screen.widgets.HeldButton;
import dev.quarris.fireandflames.data.tool.material.ToolMaterial;
import dev.quarris.fireandflames.data.tool.material.IMaterialHolder;
import dev.quarris.fireandflames.network.payload.SBSmithingAnvilHammer;
import dev.quarris.fireandflames.setup.SoundSetup;
import dev.quarris.fireandflames.world.inventory.crafting.SmithingAnvilRecipe;
import dev.quarris.fireandflames.world.inventory.menu.SmithingAnvilMenu;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class SmithingAnvilScreen extends AbstractContainerScreen<SmithingAnvilMenu> {

    private static final ResourceLocation BACKGROUND = ModRef.res("textures/gui/container/smithing_anvil.png");
    private static final WidgetSprites HAMMER_SPRITES = new WidgetSprites(ModRef.res("container/smithing_anvil/hammer"), ModRef.res("container/smithing_anvil/hammer_disabled"), ModRef.res("container/smithing_anvil/hammer_hover"));
    private static final ResourceLocation HAMMER_HELD = ModRef.res("container/smithing_anvil/hammer_clicked");
    private static final ResourceLocation PROGRESS_BAR = ModRef.res("container/smithing_anvil/progress_bar");
    private static final ResourceLocation PROGRESS_BAR_BACKGROUND = ModRef.res("container/smithing_anvil/progress_bar_background");

    private static final int RECIPE_START_X = 17;
    private static final int RECIPE_START_Y = 53;
    private static final int RECIPE_WIDTH = 140;
    private static final int RECIPE_BAR_HEIGHT = 5;
    private static final int RECIPE_HEIGHT = 16 + RECIPE_BAR_HEIGHT;

    private SoundInstance hitSound;
    private SoundInstance endSound;

    private long startTime;
    private HeldButton heldButton;

    public SmithingAnvilScreen(SmithingAnvilMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        this.imageWidth = 176;
        this.imageHeight = 186;
        this.inventoryLabelY = this.imageHeight - 94;
        super.init();

        this.heldButton = this.addRenderableWidget(new HeldButton(this.leftPos + this.imageWidth / 2 - 7, this.topPos + 20, 15, 15, HAMMER_SPRITES, HAMMER_HELD, this::startHammering, this::endHammering));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        this.renderHoveredOutput(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int recipeStartX = this.leftPos + RECIPE_START_X;
        int recipeStartY = this.topPos + RECIPE_START_Y;
        int iconSize = 16;
        int halfIconSize = iconSize / 2;

        guiGraphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        guiGraphics.blitSprite(PROGRESS_BAR_BACKGROUND, recipeStartX, recipeStartY, RECIPE_WIDTH, RECIPE_BAR_HEIGHT);

        RecipeHolder<SmithingAnvilRecipe> recipe = this.menu.getRecipe();
        if (recipe != null) {
            Holder<ToolMaterial> material = null;
            if (this.menu.getSlot(0).getItem().getItem() instanceof IMaterialHolder materialHolder) {
                material = materialHolder.getMaterial(this.menu.getSlot(0).getItem());
            }

            List<ItemStack> outputs = recipe.value().createOutputs(material);
            int outputAmount = outputs.size();
            float outputWidth = RECIPE_WIDTH / (float) outputAmount;
            for (int i = 0; i < outputAmount; i++) {
                ItemStack output = outputs.get(i);
                guiGraphics.renderItem(output, recipeStartX + (int) ((i + 0.5f) * outputWidth - halfIconSize), recipeStartY - iconSize);
            }

            for (int i = 0; i < outputAmount - 1; i++) {
                int dividerX = recipeStartX + (int) ((i + 1) * outputWidth);
                guiGraphics.fill(dividerX, recipeStartY - iconSize, dividerX + 1, recipeStartY, 0xff000000);
            }

            if (this.startTime > 0) {
                long currentTime = Util.getMillis();
                int width = Math.min(140, Mth.lerpInt((currentTime - this.startTime) / (outputAmount * 250f), 0, 140));
                guiGraphics.blitSprite(PROGRESS_BAR, recipeStartX, recipeStartY, width, 5);
            }
        }
    }

    public void renderHoveredOutput(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (this.menu.getRecipe() == null) return;
        if (mouseX < this.leftPos + RECIPE_START_X || mouseX >= this.leftPos + RECIPE_START_X + RECIPE_WIDTH || mouseY < this.topPos + RECIPE_START_Y - 16 || mouseY >= this.topPos + RECIPE_START_Y + RECIPE_BAR_HEIGHT) {
            return;
        }

        Holder<ToolMaterial> material = null;
        if (this.menu.getSlot(0).getItem().getItem() instanceof IMaterialHolder materialHolder) {
            material = materialHolder.getMaterial(this.menu.getSlot(0).getItem());
        }

        List<ItemStack> outputs = this.menu.getRecipe().value().createOutputs(material);
        int outputAmount = outputs.size();
        int outputWidth = RECIPE_WIDTH / outputAmount;
        int index = (mouseX - (this.leftPos + RECIPE_START_X)) / outputWidth;
        guiGraphics.renderTooltip(this.font, outputs.get(index), mouseX, mouseY);
    }

    @Override
    protected void containerTick() {
        this.heldButton.active = this.menu.getRecipe() != null;
        if (this.endSound != null && !this.minecraft.getSoundManager().isActive(this.endSound)) {
            this.endSound = null;
        }

        RecipeHolder<SmithingAnvilRecipe> recipe = this.menu.getRecipe();
        if (recipe != null) {
            int outputs = recipe.value().possibleOutputs().size();
            if (this.startTime > 0) {
                if (Util.getMillis() > this.startTime + outputs * 250f) {
                    this.heldButton.forceStop(false);
                    this.startTime = -1;
                    this.minecraft.getSoundManager().stop(this.hitSound);
                    float pitch = 0.95f + (float) Math.random() * 0.1f;
                    this.endSound = SimpleSoundInstance.forUI(SoundSetup.SMITHING_HIT_FAIL.get(), pitch, 1.0f);
                    this.minecraft.getSoundManager().play(this.endSound);
                }
            }
        }
    }

    public void startHammering(Button button) {
        if (this.endSound != null) {
            this.minecraft.getSoundManager().stop(this.endSound);
        }

        this.startTime = Util.getMillis();
        float pitch = 0.95f + (float) Math.random() * 0.1f;
        float volume = 0.95f + (float) Math.random() * 0.05f;
        this.hitSound = new SimpleSoundInstance(SoundSetup.SMITHING_HIT.getId(), SoundSource.PLAYERS, volume, pitch, this.minecraft.level.random, true, 0, SoundInstance.Attenuation.NONE, 0, 0, 0, true);
        this.minecraft.getSoundManager().play(this.hitSound);
    }

    public void endHammering(Button button) {
        RecipeHolder<SmithingAnvilRecipe> recipe = this.menu.getRecipe();
        if (recipe == null) {
            return;
        }

        int outputs = recipe.value().possibleOutputs().size();
        long currentTime = Util.getMillis();
        float perc = (currentTime - this.startTime) / (outputs * 250f);
        int index = Math.min((int) (perc * outputs), outputs - 1);

        if (this.menu.onHammerHit(index)) {
            PacketDistributor.sendToServer(new SBSmithingAnvilHammer(index));
            this.minecraft.getSoundManager().stop(this.hitSound);
            this.startTime = -1;

            float pitch = 0.95f + (float) Math.random() * 0.1f;
            this.endSound = SimpleSoundInstance.forUI(SoundSetup.SMITHING_FINAL.get(), pitch, 1.0f);
            this.minecraft.getSoundManager().play(this.endSound);
        }

    }
}

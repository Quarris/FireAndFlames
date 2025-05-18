package dev.quarris.fireandflames.compat.jei.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

public class DrawableEntity implements IDrawable {

    private final ITickTimer cycleTimer;
    private final HolderSet<EntityType<?>> entities;
    private final int size;

    private final Quaternionf defaultRotation;
    private final ITickTimer rotationTimer;

    public DrawableEntity(IGuiHelper guiHelper, HolderSet<EntityType<?>> entitySet, int size, @Nullable ITickTimer rotationTimer, @Nullable Quaternionf defaultRotation) {
        this.entities = entitySet;
        this.size = size;
        this.cycleTimer = guiHelper.createTickTimer(20 * entitySet.size(), entitySet.size(), false);
        this.defaultRotation = defaultRotation;
        this.rotationTimer = rotationTimer;
    }

    @Override
    public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
        int light = LightTexture.pack(15, 15);
        int cycleValue = Math.max(0, this.cycleTimer.getValue() - 1);
        EntityType<?> entityType = this.entities.get(cycleValue).value();
        Entity entity = entityType.create(Minecraft.getInstance().level);

        PoseStack matrix = guiGraphics.pose();
        matrix.pushPose(); {
            matrix.translate(xOffset, yOffset, 1000);
            matrix.translate(this.size / 2f, this.size, 0);
            float entitySize = Math.max(entityType.getWidth(), entityType.getHeight());
            matrix.scale(this.size / entitySize, -this.size / entitySize, this.size / entitySize);

            if (this.rotationTimer != null) {
                matrix.rotateAround(Axis.YP.rotationDegrees(this.rotationTimer.getValue()), 0, 0, 0);
            }

            if (this.defaultRotation != null) {
                matrix.rotateAround(this.defaultRotation, 0, 0, 0);
            }

            Player player = Minecraft.getInstance().player;
            if (entityType == EntityType.PLAYER && player != null) {
                EntityRenderer<Player> playerRenderer = (EntityRenderer<Player>) Minecraft.getInstance().getEntityRenderDispatcher().getSkinMap().get(PlayerSkin.Model.WIDE);
                playerRenderer.render(player, 0, 0, matrix, Minecraft.getInstance().renderBuffers().bufferSource(), light);
            }

            if (entity != null) {
                Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity).render(entity, 0, 0, matrix, Minecraft.getInstance().renderBuffers().bufferSource(), light);
            }
        }
        matrix.popPose();
    }

    @Override
    public int getWidth() {
        return this.size;
    }

    @Override
    public int getHeight() {
        return this.size;
    }
}

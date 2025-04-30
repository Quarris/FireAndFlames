package dev.quarris.fireandflames.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.quarris.fireandflames.world.block.entity.CrucibleControllerBlockEntity;
import dev.quarris.fireandflames.world.crucible.CrucibleFluidTank;
import dev.quarris.fireandflames.world.crucible.CrucibleStructure;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.textures.FluidSpriteCache;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class CrucibleControllerRenderer implements BlockEntityRenderer<CrucibleControllerBlockEntity> {

    public CrucibleControllerRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CrucibleControllerBlockEntity pCrucible, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pLight, int pOverlay) {
        if (pCrucible.getLevel() == null || pCrucible.getStructure() == null) return;

        CrucibleStructure.CrucibleShape shape = pCrucible.getStructure().getShape();
        BlockPos structurePosition = shape.position();

        CrucibleFluidTank tank = pCrucible.getFluidTank();
        if (tank.getTanks() > 0) {
            BlockPos cruciblePos = pCrucible.getStructure().getControllerPosition();
            BlockPos structurePos = shape.position();
            BlockPos basePos = shape.position().subtract(pCrucible.getStructure().getControllerPosition());
            pPoseStack.pushPose();
            pPoseStack.translate(0, -cruciblePos.getY() + structurePos.getY(), 0);
            float fluidHeight = 1;
            for (int fluidSlot = 0; fluidSlot < tank.getTanks(); fluidSlot++) {
                FluidStack fluidStack = tank.getFluidInTank(fluidSlot);
                FluidState state = fluidStack.getFluid().defaultFluidState();
                RenderType rendertype = ItemBlockRenderTypes.getRenderLayer(state);
                VertexConsumer buffer = pBufferSource.getBuffer(rendertype);
                IClientFluidTypeExtensions attributes = IClientFluidTypeExtensions.of(state);
                float lastFluidHeight = fluidHeight;
                fluidHeight += tank.getRelativeFluidAmount(fluidSlot) * (shape.height() - 1);

                for (int x = 1; x < shape.width() - 1; x++) {
                    for (int z = 1; z < shape.depth() - 1; z++) {
                        // Render Top
                        BlockPos relativePos = basePos.offset(x, (int) Math.floor(fluidHeight), z);
                        BlockPos blockPos = pCrucible.getBlockPos().offset(relativePos);
                        int light = LevelRenderer.getLightColor(pCrucible.getLevel(), blockPos);
                        Vec3 renderVec = Vec3.atLowerCornerOf(relativePos);
                        TextureAtlasSprite[] sprites = FluidSpriteCache.getFluidSprites(pCrucible.getLevel(), blockPos, state);
                        TextureAtlasSprite sprite = sprites[0];
                        VertexConsumer spriteBuffer = sprite.wrap(buffer);
                        int color = attributes.getTintColor(state, pCrucible.getLevel(), blockPos);
                        PoseStack.Pose pose = pPoseStack.last();
                        spriteBuffer.addVertex(pose, (float) renderVec.x(), fluidHeight, (float) (renderVec.z() + 1)).setColor(color).setUv(0, 1).setLight(light).setNormal(pose, 0, 1, 0);
                        spriteBuffer.addVertex(pose, (float) (renderVec.x() + 1), fluidHeight, (float) (renderVec.z() + 1)).setColor(color).setUv(1, 1).setLight(light).setNormal(pose, 0, 1, 0);
                        spriteBuffer.addVertex(pose, (float) (renderVec.x() + 1), fluidHeight, (float) renderVec.z()).setColor(color).setUv(1, 0).setLight(light).setNormal(pose, 0, 1, 0);
                        spriteBuffer.addVertex(pose, (float) renderVec.x(), fluidHeight, (float) renderVec.z()).setColor(color).setUv(0, 0).setLight(light).setNormal(pose, 0, 1, 0);

                        for (float startY = lastFluidHeight; startY < fluidHeight; startY++) {
                            float v0, v1 = 1;
                            relativePos = basePos.offset(x, (int) Math.floor(startY), z);
                            blockPos = pCrucible.getBlockPos().offset(relativePos);
                            light = LevelRenderer.getLightColor(pCrucible.getLevel(), blockPos);
                            renderVec = Vec3.atLowerCornerOf(relativePos);
                            sprites = FluidSpriteCache.getFluidSprites(pCrucible.getLevel(), blockPos, state);
                            boolean usesOverlay = true;
                            sprite = sprites[2];
                            if (sprite == null) {
                                sprite = sprites[1];
                                usesOverlay = false;
                            }
                            spriteBuffer = sprite.wrap(buffer);
                            color = attributes.getTintColor(state, pCrucible.getLevel(), blockPos);

                            float y = startY + Math.min(1, fluidHeight - startY);
                            v0 = Math.max(0, 1 - (y - startY));

                            float u1 = 1f;
                            if (!usesOverlay) {
                                u1 /= 2;
                                v0 /= 2;
                                v1 /= 2;
                            }

                            // Render South
                            if (z == shape.depth() - 2) {
                                spriteBuffer.addVertex(pose, (float) renderVec.x(), startY, (float) (renderVec.z() + 1)).setColor(color).setUv(0, v1).setLight(light).setNormal(pose, 0, 1, 0);
                                spriteBuffer.addVertex(pose, (float) (renderVec.x() + 1), startY, (float) (renderVec.z() + 1)).setColor(color).setUv(u1, v1).setLight(light).setNormal(pose, 0, 1, 0);
                                spriteBuffer.addVertex(pose, (float) (renderVec.x() + 1), y, (float) renderVec.z() + 1).setColor(color).setUv(u1, v0).setLight(light).setNormal(pose, 0, 1, 0);
                                spriteBuffer.addVertex(pose, (float) renderVec.x(), y, (float) renderVec.z() + 1).setColor(color).setUv(0, v0).setLight(light).setNormal(pose, 0, 1, 0);
                            }
                            // Render West
                            if (x == 1) {
                                spriteBuffer.addVertex(pose, (float) renderVec.x(), startY, (float) (renderVec.z())).setColor(color).setUv(0, v1).setLight(light).setNormal(pose, 0, 1, 0);
                                spriteBuffer.addVertex(pose, (float) (renderVec.x()), startY, (float) (renderVec.z() + 1)).setColor(color).setUv(u1, v1).setLight(light).setNormal(pose, 0, 1, 0);
                                spriteBuffer.addVertex(pose, (float) (renderVec.x()), y, (float) renderVec.z() + 1).setColor(color).setUv(u1, v0).setLight(light).setNormal(pose, 0, 1, 0);
                                spriteBuffer.addVertex(pose, (float) renderVec.x(), y, (float) renderVec.z()).setColor(color).setUv(0, v0).setLight(light).setNormal(pose, 0, 1, 0);
                            }

                            // Render North
                            if (z == 1) {
                                spriteBuffer.addVertex(pose, (float) renderVec.x() + 1, startY, (float) (renderVec.z())).setColor(color).setUv(0, v1).setLight(light).setNormal(pose, 0, 1, 0);
                                spriteBuffer.addVertex(pose, (float) (renderVec.x()), startY, (float) (renderVec.z())).setColor(color).setUv(u1, v1).setLight(light).setNormal(pose, 0, 1, 0);
                                spriteBuffer.addVertex(pose, (float) (renderVec.x()), y, (float) renderVec.z()).setColor(color).setUv(u1, v0).setLight(light).setNormal(pose, 0, 1, 0);
                                spriteBuffer.addVertex(pose, (float) renderVec.x() + 1, y, (float) renderVec.z()).setColor(color).setUv(0, v0).setLight(light).setNormal(pose, 0, 1, 0);
                            }

                            // Render East
                            if (x == shape.width() - 2) {
                                spriteBuffer.addVertex(pose, (float) (renderVec.x() + 1), startY, (float) (renderVec.z())).setColor(color).setUv(u1, v1).setLight(light).setNormal(pose, 0, 1, 0);
                                spriteBuffer.addVertex(pose, (float) (renderVec.x() + 1), y, (float) renderVec.z()).setColor(color).setUv(u1, v0).setLight(light).setNormal(pose, 0, 1, 0);
                                spriteBuffer.addVertex(pose, (float) renderVec.x() + 1, y, (float) renderVec.z() + 1).setColor(color).setUv(0, v0).setLight(light).setNormal(pose, 0, 1, 0);
                                spriteBuffer.addVertex(pose, (float) renderVec.x() + 1, startY, (float) (renderVec.z() + 1)).setColor(color).setUv(0, v1).setLight(light).setNormal(pose, 0, 1, 0);
                            }
                        }
                    }
                }
            }

            pPoseStack.popPose();
        }

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStackHandler inventory = pCrucible.getInventory();

        pPoseStack.pushPose();
        pPoseStack.translate(-pCrucible.getBlockPos().getX(), -pCrucible.getBlockPos().getY(), -pCrucible.getBlockPos().getZ());
        pPoseStack.translate(structurePosition.getX() + 1.5, structurePosition.getY() + 1.5, structurePosition.getZ() + 1.5);
        int internalWidth = shape.width() - 2;
        int internalDepth = shape.depth() - 2;
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack.isEmpty()) continue;
            int c = LevelRenderer.getLightColor(pCrucible.getLevel(), pCrucible.getBlockState(), pCrucible.getBlockPos());
            int x = slot % internalWidth;
            int y = slot / (internalWidth * internalDepth);
            int z = (slot % (internalWidth * internalDepth)) / internalWidth;
            pPoseStack.pushPose();
            pPoseStack.translate(x, y, z);
            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, c, pOverlay, pPoseStack, pBufferSource, pCrucible.getLevel(), 0);
            pPoseStack.popPose();
        }
        pPoseStack.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(CrucibleControllerBlockEntity pCrucible) {
        if (pCrucible.getStructure() == null) return BlockEntityRenderer.super.getRenderBoundingBox(pCrucible);

        BoundingBox bounds = pCrucible.getStructure().getShape().toBoundingBox();
        return AABB.of(bounds);
    }
}

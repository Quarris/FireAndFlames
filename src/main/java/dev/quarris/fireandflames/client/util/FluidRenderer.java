package dev.quarris.fireandflames.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.textures.FluidSpriteCache;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

public class FluidRenderer {

    public static Pair<TextureAtlasSprite, VertexConsumer> getFluidSpriteBuffer(Level pLevel, BlockPos pPos, FluidStack pStack, MultiBufferSource pBufferSource, RenderType pRenderType, FluidSpriteType type, FluidSpriteType backup) {
        FluidState state = pStack.getFluid().defaultFluidState();
        VertexConsumer buffer = pBufferSource.getBuffer(pRenderType);
        TextureAtlasSprite[] sprites = FluidSpriteCache.getFluidSprites(pLevel, pPos, state);
        TextureAtlasSprite sprite = sprites[type.typeIndex];
        if (sprite == null) {
            sprite = sprites[backup.typeIndex];
        }
        return Pair.of(sprite, sprite.wrap(buffer));
    }

    public static int getFluidColor(Level pLevel, BlockPos pPos, FluidState pState) {
        IClientFluidTypeExtensions attributes = IClientFluidTypeExtensions.of(pState);
        return attributes.getTintColor(pState, pLevel, pPos);
    }

    public static int getFluidColor(FluidStack pStack) {
        IClientFluidTypeExtensions attributes = IClientFluidTypeExtensions.of(pStack.getFluidType());
        return attributes.getTintColor(pStack);
    }

    public static void renderFluidFace(VertexConsumer pBuffer, PoseStack pPoseStack, Vec3[] pPositions, int pColor, int pLight) {
        renderFluidFace(pBuffer, pPoseStack, pPositions, new int[]{pColor, pColor, pColor, pColor}, pLight);
    }

    public static void renderFluidFace(VertexConsumer pBuffer, PoseStack pPoseStack, Vec3[] pPositions, int[] pColors, int pLight) {
        PoseStack.Pose pose = pPoseStack.last();
        Vec3 pos0 = pPositions[0];
        Vec3 pos1 = pPositions[1];
        Vec3 pos2 = pPositions[2];
        Vec3 pos3 = pPositions[3];
        pBuffer.addVertex(pose, (float) pos0.x(), (float) pos0.y(), (float) pos0.z()).setColor(pColors[0]).setUv(0, 1).setLight(pLight).setNormal(pose, 0, 1, 0);
        pBuffer.addVertex(pose, (float) pos1.x(), (float) pos1.y(), (float) pos1.z()).setColor(pColors[1]).setUv(1, 1).setLight(pLight).setNormal(pose, 0, 1, 0);
        pBuffer.addVertex(pose, (float) pos2.x(), (float) pos2.y(), (float) pos2.z()).setColor(pColors[2]).setUv(1, 0).setLight(pLight).setNormal(pose, 0, 1, 0);
        pBuffer.addVertex(pose, (float) pos3.x(), (float) pos3.y(), (float) pos3.z()).setColor(pColors[3]).setUv(0, 0).setLight(pLight).setNormal(pose, 0, 1, 0);
    }

    public enum FluidSpriteType {
        STILL(0), FLOWING(1), OVERLAY(2);

        public final int typeIndex;

        FluidSpriteType(int typeIndex) {
            this.typeIndex = typeIndex;
        }
    }

}

package dev.quarris.fireandflames.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.quarris.fireandflames.client.util.FluidRenderer;
import dev.quarris.fireandflames.world.block.CrucibleFawsitBlock;
import dev.quarris.fireandflames.world.block.entity.CrucibleFawsitBlockEntity;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;

public class CrucibleFawsitRenderer implements BlockEntityRenderer<CrucibleFawsitBlockEntity> {

    public CrucibleFawsitRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CrucibleFawsitBlockEntity pFawsit, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pLight, int pOverlay) {
        if (!pFawsit.isActive()) {
            return;
        }

        FluidStack activeFluid = pFawsit.getActiveFluid();
        Direction facing = pFawsit.getBlockState().getValue(CrucibleFawsitBlock.FACING);

        pPoseStack.pushPose();
        pPoseStack.translate(0.5, 0, 0.5);
        pPoseStack.rotateAround(Axis.YP.rotationDegrees(-facing.toYRot() - 90), 0, -1, 0);
        pPoseStack.translate(-0.5, 0.5, -0.5);
        VertexConsumer flowingBuffer = FluidRenderer.getFluidSpriteBuffer(pFawsit.getLevel(), pFawsit.getBlockPos(), activeFluid, pBufferSource, RenderType.translucent(), FluidRenderer.FluidSpriteType.FLOWING, FluidRenderer.FluidSpriteType.STILL).getRight();
        int color = FluidRenderer.getFluidColor(pFawsit.getLevel(), pFawsit.getBlockPos(), activeFluid.getFluid().defaultFluidState());
        // Top
        FluidRenderer.renderFluidFace(flowingBuffer, pPoseStack, new Vec3[]{
            new Vec3(10 / 16f, -0.1f, 11 / 16f),
            new Vec3(10 / 16f, -0.1f, 5 / 16f),
            new Vec3(0, 0, 5 / 16f),
            new Vec3(0, 0, 11 / 16f),
        }, color, pLight);

        var fadedColor = color & 0x00FFFFFF;
        // Front
        FluidRenderer.renderFluidFace(flowingBuffer, pPoseStack,
            new Vec3[]{
                new Vec3(10 / 16f, -1f, 11 / 16f),
                new Vec3(10 / 16f, -1f, 5 / 16f),
                new Vec3(10 / 16f, -0.1f, 5 / 16f),
                new Vec3(10 / 16f, -0.1f, 11 / 16f),
            }, new int[]{
                fadedColor, fadedColor, color, color
            }, pLight);


        // Right
        FluidRenderer.renderFluidFace(flowingBuffer, pPoseStack,
            new Vec3[]{
                new Vec3(10 / 16f, -1, 5 / 16f),
                new Vec3(6 / 16f, -1, 5 / 16f),
                new Vec3(6 / 16f, -0.06f, 5 / 16f),
                new Vec3(10 / 16f, -0.1f, 5 / 16f),
            },
            new int[]{
                fadedColor, fadedColor, color, color
            }, pLight);

        // Left
        FluidRenderer.renderFluidFace(flowingBuffer, pPoseStack,
            new Vec3[]{
                new Vec3(6 / 16f, -1, 11 / 16f),
                new Vec3(10 / 16f, -1, 11 / 16f),
                new Vec3(10 / 16f, -0.1f, 11 / 16f),
                new Vec3(6 / 16f, -0.06f, 11 / 16f),
            },
            new int[]{
                fadedColor, fadedColor, color, color
            }, pLight);

        // Back
        FluidRenderer.renderFluidFace(flowingBuffer, pPoseStack,
            new Vec3[]{
                new Vec3(6 / 16f, -1, 5 / 16f),
                new Vec3(6 / 16f, -1, 11 / 16f),
                new Vec3(6 / 16f, -3 / 16f, 11 / 16f),
                new Vec3(6 / 16f, -3 / 16f, 5 / 16f),
            },
            new int[]{
                fadedColor, fadedColor, color, color
            }, pLight);
        pPoseStack.popPose();
    }
}

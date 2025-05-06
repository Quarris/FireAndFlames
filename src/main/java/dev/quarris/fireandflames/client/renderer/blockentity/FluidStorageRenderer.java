package dev.quarris.fireandflames.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.quarris.fireandflames.client.util.FluidRenderer;
import dev.quarris.fireandflames.world.block.entity.FluidStorageBlockEntity;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class FluidStorageRenderer implements BlockEntityRenderer<FluidStorageBlockEntity> {

    public FluidStorageRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(FluidStorageBlockEntity pStorage, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pLight, int pOverlay) {
        FluidTank tank = pStorage.getFluidTank();
        FluidStack fluidStack = tank.getFluid();
        if (!fluidStack.isEmpty()) {
            assert pStorage.getLevel() != null;
            BlockPos blockPos = pStorage.getBlockPos();
            FluidState state = fluidStack.getFluid().defaultFluidState();
            VertexConsumer spriteBuffer = FluidRenderer.getFluidSpriteBuffer(pStorage.getLevel(), blockPos, fluidStack, pBufferSource, ItemBlockRenderTypes.getRenderLayer(state), FluidRenderer.FluidSpriteType.STILL, FluidRenderer.FluidSpriteType.FLOWING).getRight();
            IClientFluidTypeExtensions attributes = IClientFluidTypeExtensions.of(state);
            int color = attributes.getTintColor(state, pStorage.getLevel(), blockPos);

            double height = Math.min(1, fluidStack.getAmount() / (double) tank.getCapacity());

            // Top
            FluidRenderer.renderFluidFace(spriteBuffer, pPoseStack, new Vec3[]{
                new Vec3(0, height, 1),
                new Vec3(1, height, 1),
                new Vec3(1, height, 0),
                new Vec3(0, height, 0)
            }, color, pLight);

            // North
            FluidRenderer.renderFluidFace(spriteBuffer, pPoseStack, new Vec3[]{
                new Vec3(1, 0, 0.01),
                new Vec3(0, 0, 0.01),
                new Vec3(0, height, 0.01),
                new Vec3(1, height, 0.01)
            }, color, pLight);

            // South
            FluidRenderer.renderFluidFace(spriteBuffer, pPoseStack, new Vec3[]{
                new Vec3(0, 0, 0.99),
                new Vec3(1, 0, 0.99),
                new Vec3(1, height, 0.99),
                new Vec3(0, height, 0.99)
            }, color, pLight);

            // East
            FluidRenderer.renderFluidFace(spriteBuffer, pPoseStack, new Vec3[]{
                new Vec3(0.99, 0, 1),
                new Vec3(0.99, 0, 0),
                new Vec3(0.99, height, 0),
                new Vec3(0.99, height, 1)
            }, color, pLight);

            // West
            FluidRenderer.renderFluidFace(spriteBuffer, pPoseStack, new Vec3[]{
                new Vec3(0.01, 0, 0),
                new Vec3(0.01, 0, 1),
                new Vec3(0.01, height, 1),
                new Vec3(0.01, height, 0)
            }, color, pLight);

        }
    }
}

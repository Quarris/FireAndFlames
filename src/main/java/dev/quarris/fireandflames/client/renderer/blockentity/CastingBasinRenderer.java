package dev.quarris.fireandflames.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.quarris.fireandflames.client.util.FluidRenderer;
import dev.quarris.fireandflames.world.block.entity.CastingBasinBlockEntity;
import dev.quarris.fireandflames.world.crucible.crafting.BasinCastingRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;

public class CastingBasinRenderer implements BlockEntityRenderer<CastingBasinBlockEntity> {

    public CastingBasinRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CastingBasinBlockEntity pBasin, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pLight, int pOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BasinCastingRecipe recipe = null;
        if (pBasin.getRecipe() != null) {
            recipe = pBasin.getRecipe().value();
        }
        float alpha = 0.0f;
        if (recipe != null) {
            alpha = pBasin.getCoolingTicks() / (float) recipe.coolingTime;
        }


        if (!pBasin.getTank().isEmpty()) {
            FluidStack stack = pBasin.getTank().getFluid();
            double height = Mth.lerp(stack.getAmount() / (double) pBasin.getTank().getCapacity(), 4 / 16.0, 15 / 16.0);
            FluidState fluidState = stack.getFluid().defaultFluidState();
            VertexConsumer spriteBuffer = FluidRenderer.getFluidSpriteBuffer(pBasin.getLevel(), pBasin.getBlockPos(), stack, pBufferSource, RenderType.translucent(), FluidRenderer.FluidSpriteType.STILL).getRight();
            int color = FluidRenderer.getFluidColor(pBasin.getLevel(), pBasin.getBlockPos(), fluidState);
            color = color & ((int) ((1 - alpha) * 255) << 24 | 0x00ffffff);
            FluidRenderer.renderFluidFace(spriteBuffer, pPoseStack, new Vec3[]{
                new Vec3(13 / 16f, height, 13 / 16f),
                new Vec3(13 / 16f, height, 3 / 16f),
                new Vec3(3 / 16f, height, 3 / 16f),
                new Vec3(3 / 16f, height, 13 / 16f),
            }, color, pLight);
        }

        int slots = pBasin.getInventory().getSlots();
        for (int slot = 0; slot < slots; slot++) {
            pPoseStack.pushPose();
            pPoseStack.translate(0.5, 0.7, 0.5);
            pPoseStack.scale(0.75f, 0.75f, 0.75f);
            ItemStack stack = pBasin.getInventory().getStackInSlot(slot);
            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, pLight, pOverlay, pPoseStack, pBufferSource, pBasin.getLevel(), 0);
            pPoseStack.popPose();
        }
    }
}

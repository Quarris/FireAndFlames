package dev.quarris.fireandflames.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.quarris.fireandflames.client.util.FluidRenderer;
import dev.quarris.fireandflames.world.block.entity.CastingBasinBlockEntity;
import dev.quarris.fireandflames.world.block.entity.CastingTableBlockEntity;
import dev.quarris.fireandflames.world.crucible.crafting.BasinCastingRecipe;
import dev.quarris.fireandflames.world.crucible.crafting.TableCastingRecipe;
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

public class CastingTableRenderer implements BlockEntityRenderer<CastingTableBlockEntity> {

    public CastingTableRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CastingTableBlockEntity pTable, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pLight, int pOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        TableCastingRecipe recipe = null;
        if (pTable.getRecipe() != null) {
            recipe = pTable.getRecipe().value();
        }

        float alpha = 1.0f;
        if (recipe != null) {
            alpha = pTable.getCoolingTicks() / (float) recipe.coolingTime;
        }


        if (!pTable.getTank().isEmpty()) {
            FluidStack stack = pTable.getTank().getFluid();
            double height = Mth.lerp(stack.getAmount() / (double) pTable.getTank().getCapacity(), 15/16.0, 15.01/16f + (1/16f) * (10/16f));
            FluidState fluidState = stack.getFluid().defaultFluidState();
            VertexConsumer spriteBuffer = FluidRenderer.getFluidSpriteBuffer(pTable.getLevel(), pTable.getBlockPos(), stack, pBufferSource, RenderType.translucent(), FluidRenderer.FluidSpriteType.STILL).getRight();
            int color = FluidRenderer.getFluidColor(pTable.getLevel(), pTable.getBlockPos(), fluidState);
            color = color & ((int) ((1 - alpha) * 255) << 24 | 0x00ffffff);
            FluidRenderer.renderFluidFace(spriteBuffer, pPoseStack, new Vec3[]{
                new Vec3(13/16f, height, 13/16f),
                new Vec3(13/16f, height, 3/16f),
                new Vec3(3/16f, height, 3/16f),
                new Vec3(3/16f, height, 13/16f),
            }, color, pLight);
        }

        int slots = pTable.getInventory().getSlots();
        for (int slot = 0; slot < slots; slot++) {
            pPoseStack.pushPose();
            pPoseStack.translate(0.5, 15/16f + (1/16f) * (10/16f) / 2, 0.5);
            pPoseStack.scale(10/16f, 10/16f, 10/16f);
            pPoseStack.rotateAround(Axis.XP.rotationDegrees(90), 0, 0, 0);
            ItemStack stack = pTable.getInventory().getStackInSlot(slot);
            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, pLight, pOverlay, pPoseStack, pBufferSource, pTable.getLevel(), 0);
            pPoseStack.popPose();
        }
    }
}

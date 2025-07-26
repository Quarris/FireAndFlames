package dev.quarris.fireandflames.client.renderer.tool;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ToolItemRenderer extends BlockEntityWithoutLevelRenderer {

    public ToolItemRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
        super(blockEntityRenderDispatcher, entityModelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel model = itemRenderer.getModel(stack, null, null, 0);
        boolean leftHand = false;

        poseStack.pushPose();
        model = net.neoforged.neoforge.client.ClientHooks.handleCameraTransforms(poseStack, model, displayContext, leftHand);
        boolean fabulousRendering = true;

        for (var pass : model.getRenderPasses(stack, fabulousRendering)) {
            for (var rendertype : pass.getRenderTypes(stack, fabulousRendering)) {
                VertexConsumer vertexconsumer;
                vertexconsumer = ItemRenderer.getFoilBufferDirect(buffer, rendertype, true, stack.hasFoil());
                itemRenderer.renderModelLists(pass, stack, packedLight, packedOverlay, poseStack, vertexconsumer);
            }
        }


        poseStack.popPose();

    }
}

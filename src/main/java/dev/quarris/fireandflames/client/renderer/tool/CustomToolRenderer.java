package dev.quarris.fireandflames.client.renderer.tool;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.quarris.fireandflames.data.tool.ToolData;
import dev.quarris.fireandflames.setup.DataComponentSetup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class CustomToolRenderer extends BlockEntityWithoutLevelRenderer {

    public CustomToolRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ToolData toolData = stack.get(DataComponentSetup.TOOL_DATA);
    }
}

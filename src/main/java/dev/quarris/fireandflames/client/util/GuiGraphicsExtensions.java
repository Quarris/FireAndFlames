package dev.quarris.fireandflames.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class GuiGraphicsExtensions {

    public static void blitTiledSprite(
        PoseStack poseStack,
        TextureAtlasSprite sprite,
        int x,
        int y,
        int blitOffset,
        int width,
        int height,
        int uPosition,
        int vPosition,
        int spriteWidth,
        int spriteHeight,
        int nineSliceWidth,
        int nineSliceHeight,
        float red,
        float green,
        float blue,
        float alpha
    ) {
        if (width > 0 && height > 0) {
            if (spriteWidth > 0 && spriteHeight > 0) {
                for (int i = 0; i < width; i += spriteWidth) {
                    int j = Math.min(spriteWidth, width - i);

                    for (int k = 0; k < height; k += spriteHeight) {
                        int l = Math.min(spriteHeight, height - k);
                        blitSprite(
                            poseStack,
                            sprite,
                            nineSliceWidth,
                            nineSliceHeight,
                            uPosition,
                            vPosition,
                            x + i,
                            y + k,
                            blitOffset,
                            j, l, red, green, blue, alpha);
                    }
                }
            } else {
                throw new IllegalArgumentException("Tiled sprite texture size must be positive, got " + spriteWidth + "x" + spriteHeight);
            }
        }
    }

    private static void blitSprite(
        PoseStack poseStack,
        TextureAtlasSprite sprite,
        int textureWidth,
        int textureHeight,
        int uPosition,
        int vPosition,
        int x,
        int y,
        int blitOffset,
        int uWidth,
        int vHeight,
        float red,
        float green,
        float blue,
        float alpha
    ) {
        if (uWidth != 0 && vHeight != 0) {
            innerBlit(
                poseStack,
                sprite.atlasLocation(),
                x,
                x + uWidth,
                y,
                y + vHeight,
                blitOffset,
                sprite.getU((float) uPosition / (float) textureWidth),
                sprite.getU((float) (uPosition + uWidth) / (float) textureWidth),
                sprite.getV((float) vPosition / (float) textureHeight),
                sprite.getV((float) (vPosition + vHeight) / (float) textureHeight),
                red, green, blue, alpha
            );
        }
    }

    static void innerBlit(
        PoseStack poseStack,
        ResourceLocation atlasLocation,
        int x1,
        int x2,
        int y1,
        int y2,
        int blitOffset,
        float minU,
        float maxU,
        float minV,
        float maxV,
        float red,
        float green,
        float blue,
        float alpha
    ) {
        RenderSystem.setShaderTexture(0, atlasLocation);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.enableBlend();
        Matrix4f matrix4f = poseStack.last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.addVertex(matrix4f, (float) x1, (float) y1, (float) blitOffset).setUv(minU, minV).setColor(red, green, blue, alpha);
        bufferbuilder.addVertex(matrix4f, (float) x1, (float) y2, (float) blitOffset).setUv(minU, maxV).setColor(red, green, blue, alpha);
        bufferbuilder.addVertex(matrix4f, (float) x2, (float) y2, (float) blitOffset).setUv(maxU, maxV).setColor(red, green, blue, alpha);
        bufferbuilder.addVertex(matrix4f, (float) x2, (float) y1, (float) blitOffset).setUv(maxU, minV).setColor(red, green, blue, alpha);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
        RenderSystem.disableBlend();
    }

}

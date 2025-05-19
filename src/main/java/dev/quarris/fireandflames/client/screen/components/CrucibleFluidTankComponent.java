package dev.quarris.fireandflames.client.screen.components;

import com.mojang.blaze3d.platform.InputConstants;
import dev.quarris.fireandflames.client.util.GuiGraphicsExtensions;
import dev.quarris.fireandflames.config.ServerConfigs;
import dev.quarris.fireandflames.world.crucible.CrucibleFluidTank;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.textures.FluidSpriteCache;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class CrucibleFluidTankComponent {

    private final Supplier<CrucibleFluidTank> fluidTankSupplier;
    private final int x, y;
    private final int width, height;

    public CrucibleFluidTankComponent(Supplier<CrucibleFluidTank> fluidTankSupplier, int x, int y, int width, int height) {
        this.fluidTankSupplier = fluidTankSupplier;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean isHovering(double pMouseX, double pMouseY) {
        return pMouseX >= this.x && pMouseX < this.x + this.width && pMouseY >= this.y && pMouseY < this.y + this.height;
    }

    public int getHoveringFluidTank(double pMouseX, double pMouseY) {
        if (!this.isHovering(pMouseX, pMouseY)) {
            return -1;
        }

        int pos = 0;
        for (int tank = 0; tank < this.fluidTankSupplier.get().getTanks(); tank++) {
            int height = this.getHeightForFluidSlot(tank);
            if (pMouseY >= this.y + this.height - pos - height && pMouseY < this.y + this.height - pos) {
                return tank;
            }
            pos += height;
        }

        return -1;
    }

    public void render(GuiGraphics pGraphics, Level pLevel, BlockPos pPos, int pMouseX, int pMouseY, float pPartialTick) {
        CrucibleFluidTank fluidTank = this.fluidTankSupplier.get();
        int tanks = fluidTank.getTanks();
        int drawY = 0;

        for (int tank = 0; tank < tanks; tank++) {
            int drawHeight = this.getHeightForFluidSlot(tank);
            FluidStack fluid = fluidTank.getFluidInTank(tank);
            renderFluidAt(pGraphics, pLevel, pPos, fluid, this.x, (this.y + this.height - drawY - drawHeight), this.width, drawHeight);
            drawY += drawHeight;
        }

        // Render Tooltip
        int hoverTank = this.getHoveringFluidTank(pMouseX, pMouseY);
        if (hoverTank < 0) {
            if (this.isHovering(pMouseX, pMouseY)) {
                pGraphics.renderTooltip(Minecraft.getInstance().font, List.of(
                    Component.translatable("container.fireandflames.crucible.fluid_tank.empty"),
                    Component.literal(this.fluidTankSupplier.get().getRemainingVolume() + "mb").withStyle(ChatFormatting.GRAY)
                ), Optional.empty(), pMouseX, pMouseY);
            }
            return;
        }

        FluidStack fluid = fluidTank.getFluidInTank(hoverTank);
        List<Component> fluidTextComponents = new ArrayList<>();
        fluidTextComponents.add(fluid.getHoverName());
        int amount = fluid.getAmount();

        if (Screen.hasShiftDown()) {
            int buckets = amount / FluidType.BUCKET_VOLUME;
            int mb = amount % FluidType.BUCKET_VOLUME;

            if (buckets > 0) {
                fluidTextComponents.add(Component.literal(String.valueOf(buckets)).append(" B").withStyle(ChatFormatting.GRAY));
            }

            if (mb > 0) {
                fluidTextComponents.add(Component.literal(String.valueOf(mb)).append(" mb").withStyle(ChatFormatting.GRAY));
            }
        } else {
            int blocks = (int) (amount / ServerConfigs.getBlockMb());
            int ingots = (int) ((amount % (ServerConfigs.getBlockMb())) / ServerConfigs.getIngotMb());
            int nuggets = (int) ((amount % (ServerConfigs.getBlockMb())) % ServerConfigs.getIngotMb() / ServerConfigs.getNuggetMb());
            int mb = (int) ((amount % (ServerConfigs.getBlockMb())) % ServerConfigs.getIngotMb() % ServerConfigs.getNuggetMb());

            if (blocks > 0) {
                fluidTextComponents.add(Component.literal(String.valueOf(blocks)).append(" Blocks").withStyle(ChatFormatting.GRAY));
            }

            if (ingots > 0) {
                fluidTextComponents.add(Component.literal(String.valueOf(ingots)).append(" Ingots").withStyle(ChatFormatting.GRAY));
            }

            if (nuggets > 0) {
                fluidTextComponents.add(Component.literal(String.valueOf(nuggets)).append(" Nuggets").withStyle(ChatFormatting.GRAY));
            }

            if (mb > 0) {
                fluidTextComponents.add(Component.literal(String.valueOf(mb)).append(" mb").withStyle(ChatFormatting.GRAY));
            }
        }

        pGraphics.renderTooltip(Minecraft.getInstance().font, fluidTextComponents, Optional.empty(), pMouseX, pMouseY);
    }

    public int getHeightForFluidSlot(int tank) {
        CrucibleFluidTank fluidTank = this.fluidTankSupplier.get();
        int maxHeight = this.getMaxDrawHeight();
        int totalStored = fluidTank.getStored();
        FluidStack fluid = fluidTank.getFluidInTank(tank);
        double ratio = fluid.getAmount() / (double) totalStored;
        return (int) Math.ceil(maxHeight * ratio);
    }

    public int getMaxDrawHeight() {
        CrucibleFluidTank fluidTank = this.fluidTankSupplier.get();
        return Mth.lerpInt((fluidTank.getCapacity() - fluidTank.getRemainingVolume()) / (float) fluidTank.getCapacity(), Math.max(9, fluidTank.getTanks()), this.height - 2);
    }

    private static void renderFluidAt(GuiGraphics pGraphics, Level pLevel, BlockPos pPos, FluidStack pFluid, int pX, int pY, int pWidth, int pHeight) {
        TextureAtlasSprite sprite = FluidSpriteCache.getFluidSprites(pLevel, pPos, pFluid.getFluid().defaultFluidState())[0];
        IClientFluidTypeExtensions fluidExtensions = IClientFluidTypeExtensions.of(pFluid.getFluidType());
        int color = fluidExtensions.getTintColor(pFluid.getFluid().defaultFluidState(), pLevel, pPos);

        float red = (float) (color >> 16 & 0xFF) / 255.0F;
        float green = (float) (color >> 8 & 0xFF) / 255.0F;
        float blue = (float) (color & 0xFF) / 255.0F;

        GuiGraphicsExtensions.blitTiledSprite(pGraphics.pose(), sprite, pX, pY, 0, pWidth, pHeight, 0, 0, sprite.contents().width(), sprite.contents().height(), sprite.contents().width(), sprite.contents().height(), red, green, blue, 1.0F);
    }
}

package dev.quarris.fireandflames.client.fluid;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

public class SimpleClientFluidExtensions implements IClientFluidTypeExtensions {

    private final ResourceLocation id;

    private final ResourceLocation stillTexture;
    private final ResourceLocation flowingTexture;

    public SimpleClientFluidExtensions(ResourceLocation id) {
        this.id = id;
        this.stillTexture = this.id.withPrefix("block/");
        this.flowingTexture = this.id.withPrefix("block/flowing_");
    }

    @Override
    public ResourceLocation getStillTexture() {
        return this.stillTexture;
    }

    @Override
    public ResourceLocation getFlowingTexture() {
        return this.flowingTexture;
    }
}

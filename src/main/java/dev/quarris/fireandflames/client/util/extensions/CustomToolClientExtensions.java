package dev.quarris.fireandflames.client.util.extensions;

import dev.quarris.fireandflames.client.renderer.tool.CustomToolRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class CustomToolClientExtensions implements IClientItemExtensions {

    private final CustomToolRenderer customToolRenderer = new CustomToolRenderer();

    @Override
    public BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return this.customToolRenderer;
    }
}

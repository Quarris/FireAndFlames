package dev.quarris.fireandflames.client.renderer.tool;

import net.minecraft.client.Minecraft;

public class ToolRenderer {

    private static ToolRenderer INSTANCE;
    public static ToolRenderer get() {
        if (INSTANCE == null) {
            INSTANCE = new ToolRenderer();
        }

        return INSTANCE;
    }

    private final ToolItemRenderer toolRenderer;

    public ToolRenderer() {
        this.toolRenderer = new ToolItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    public static void init() {
        get();
    }

    public ToolItemRenderer getRenderer() {
        return this.toolRenderer;
    }
}

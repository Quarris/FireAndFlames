package dev.quarris.fireandflames.client;

import dev.quarris.fireandflames.client.renderer.tool.CustomToolRenderer;
import net.minecraft.client.Minecraft;

public class ModClient {

    private static ModClient INSTANCE;

    public static ModClient get() {
        if (INSTANCE == null) {
            INSTANCE = new ModClient();
        }

        return INSTANCE;
    }

    private CustomToolRenderer customToolRenderer;

    public void init(Minecraft mc) {
        this.customToolRenderer = new CustomToolRenderer();
    }

    public CustomToolRenderer getCustomToolRenderer() {
        return this.customToolRenderer;
    }
}

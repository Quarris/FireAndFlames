package dev.quarris.fireandflames.datagen.server;

import dev.quarris.fireandflames.data.tool.ToolMaterial;
import dev.quarris.fireandflames.setup.MaterialSetup;
import net.minecraft.data.worldgen.BootstrapContext;

public class MaterialGen {

    public static void bootstrap(BootstrapContext<ToolMaterial> ctx) {
        ctx.register(MaterialSetup.WOOD, new ToolMaterial("Wood"));
        ctx.register(MaterialSetup.STONE, new ToolMaterial("Stone"));
        ctx.register(MaterialSetup.COPPER, new ToolMaterial("Copper"));
        ctx.register(MaterialSetup.IRON, new ToolMaterial("Iron"));
        ctx.register(MaterialSetup.GOLD, new ToolMaterial("Gold"));
    }

}

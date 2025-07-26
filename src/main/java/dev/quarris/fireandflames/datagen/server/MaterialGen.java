package dev.quarris.fireandflames.datagen.server;

import dev.quarris.fireandflames.data.tool.material.MaterialColor;
import dev.quarris.fireandflames.data.tool.material.ToolMaterial;
import dev.quarris.fireandflames.setup.MaterialSetup;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.BlockTags;

public final class MaterialGen {

    public static void bootstrap(BootstrapContext<ToolMaterial> ctx) {
        ctx.register(MaterialSetup.WOOD, new ToolMaterial("Wood", 59, 0, BlockTags.INCORRECT_FOR_WOODEN_TOOL,
            2.0f, 0.0f, 1.0f, 0.0f, 0.2f, new MaterialColor(117, 88, 33)));
        ctx.register(MaterialSetup.STONE, new ToolMaterial("Stone", 131, 0, BlockTags.INCORRECT_FOR_STONE_TOOL,
            4.0f, 0.0f, 1.0f, 1.0f, 0.0f, new MaterialColor(137, 137, 137)));
        ctx.register(MaterialSetup.FLINT, new ToolMaterial("Flint", 153, 0, BlockTags.INCORRECT_FOR_STONE_TOOL,
            4.0f, 0.5f, 1.2f, 0.5f, 0.2f, new MaterialColor(51, 50, 50)));
        ctx.register(MaterialSetup.COPPER, new ToolMaterial("Copper", 201, 0, BlockTags.INCORRECT_FOR_STONE_TOOL,
            5.0f, 0.0f, 1.0f, 0.5f, 0.5f, new MaterialColor(231, 124, 86)));
        ctx.register(MaterialSetup.IRON, new ToolMaterial("Iron", 250, 30, BlockTags.INCORRECT_FOR_IRON_TOOL,
            6.0f, 0.0f, 1.0f, 2.0f, 0.4f, new MaterialColor(209, 209, 209)));
        ctx.register(MaterialSetup.GOLD, new ToolMaterial("Gold", 32, -10, BlockTags.INCORRECT_FOR_GOLD_TOOL,
            12.0f, 0.1f, 1.0f, 0.0f, 1.0f, new MaterialColor(247, 198, 52)));
        ctx.register(MaterialSetup.OBSIDIAN, new ToolMaterial("Obsidian", 1096, 100, BlockTags.INCORRECT_FOR_GOLD_TOOL,
            6.0f, 0.0f, 1.0f, 0.0f, 0.0f, new MaterialColor(39, 30, 61)));
        ctx.register(MaterialSetup.NETHERITE, new ToolMaterial("Netherite", 2031, 50, BlockTags.INCORRECT_FOR_GOLD_TOOL,
            9.0f, 0.0f, 1.0f, 4.0f, 0.75f, new MaterialColor(79, 60, 62)));
    }
}

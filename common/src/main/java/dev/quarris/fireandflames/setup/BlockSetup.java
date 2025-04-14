package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.platform.Services;
import dev.quarris.fireandflames.platform.services.IRegistryHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Supplier;

public class BlockSetup {

    private static final IRegistryHelper REGISTRY = Services.REGISTRY;
    public static final Supplier<Block> FIRE_CLAY = registerBlock("fire_clay",
        () -> new Block(BlockBehaviour.Properties.of()
            .mapColor(MapColor.TERRACOTTA_RED)
            .strength(0.6F)
            .sound(SoundType.GRAVEL)), new Item.Properties());

    // Helper methods
    private static <T extends Block> Supplier<T> registerBlock(String name, Supplier<T> blockSupplier, Item.Properties itemProperties) {
        return REGISTRY.registerBlock(name, blockSupplier, itemProperties);
    }

    private static <T extends Block> Supplier<T> registerBlockNoItem(String name, Supplier<T> blockSupplier) {
        return REGISTRY.registerBlockNoItem(name, blockSupplier);
    }
    public static void init() {}
}

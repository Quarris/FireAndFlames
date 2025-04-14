package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.platform.Services;
import dev.quarris.fireandflames.platform.services.IRegistryHelper;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class ItemSetup {
    private static final IRegistryHelper REGISTRY = Services.REGISTRY;
    public static final Supplier<Item> FIRE_CLAY_BALL = registerItem("fire_clay_ball",
        () -> new Item(new Item.Properties()));

    // Fire Brick - Basic crafting material for smelter structures
    public static final Supplier<Item> FIRE_BRICK = registerItem("fire_brick",
        () -> new Item(new Item.Properties()));

    // Helper method
    private static <T extends Item> Supplier<T> registerItem(String name, Supplier<T> itemSupplier) {
        return REGISTRY.registerItem(name, itemSupplier);
    }

    public static void init() {
    }
}

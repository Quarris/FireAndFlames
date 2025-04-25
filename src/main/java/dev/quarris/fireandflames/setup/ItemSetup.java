package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class ItemSetup {
    public static final DeferredRegister.Items REGISTRY = DeferredRegister.Items.createItems(ModRef.ID);
    public static final DeferredItem<Item> FIRE_CLAY_BALL = registerItem("fire_clay_ball", Item::new);

    // Fire Brick - Basic crafting material for smelter structures
    public static final DeferredItem<Item> FIRE_BRICK = registerItem("fire_brick", Item::new);

    // Helper method
    public static <T extends Item> DeferredItem<T> registerItem(String name, Function<Item.Properties, T> itemSupplier) {
        return REGISTRY.registerItem(name, itemSupplier);
    }

    public static <T extends Item> DeferredItem<T> registerItem(String name, Function<Item.Properties, T> itemSupplier, Item.Properties props) {
        return REGISTRY.registerItem(name, itemSupplier, props);
    }

    public static void init(IEventBus modBus) {
        REGISTRY.register(modBus);
    }
}

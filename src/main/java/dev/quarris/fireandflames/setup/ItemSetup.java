package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public class ItemSetup {
    public static final DeferredRegister.Items REGISTRY = DeferredRegister.Items.createItems(ModRef.ID);
    public static final DeferredItem<Item> FIRE_CLAY_BALL = registerItem("fire_clay_ball", Item::new);

    public static final DeferredItem<Item> FIRE_BRICK = registerItem("fire_brick", Item::new);
    public static final DeferredItem<Item> INGOT_CAST = registerItem("ingot_cast", Item::new);
    public static final DeferredItem<Item> NUGGET_CAST = registerItem("nugget_cast", Item::new);

    // Helper method
    public static <T extends Item> DeferredItem<T> registerItem(String name, Supplier<T> itemSupplier) {
        return REGISTRY.register(name, itemSupplier);
    }

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

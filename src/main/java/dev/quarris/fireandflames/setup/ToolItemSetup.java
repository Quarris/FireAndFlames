package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.tool.MaterialTier;
import dev.quarris.fireandflames.data.tool.part.ToolPart;
import dev.quarris.fireandflames.world.item.tool.*;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public class ToolItemSetup {

    public static final DeferredRegister.Items REGISTRY = DeferredRegister.Items.createItems(ModRef.ID);

    public static final DeferredItem<PickaxeToolItem> PICKAXE = registerItem("pickaxe", () -> new PickaxeToolItem(ToolTypeSetup.PICKAXE, new MaterialTier(), new Item.Properties().durability(1)));
    public static final DeferredItem<AxeToolItem> AXE = registerItem("axe", () -> new AxeToolItem(new MaterialTier(), new Item.Properties().durability(1)));
    public static final DeferredItem<ShovelToolItem> SHOVEL = registerItem("shovel", () -> new ShovelToolItem(ToolTypeSetup.SHOVEL, new MaterialTier(), new Item.Properties().durability(1)));
    public static final DeferredItem<HoeToolItem> HOE = registerItem("hoe", () -> new HoeToolItem(ToolTypeSetup.HOE, new MaterialTier(), new Item.Properties().durability(1)));
    public static final DeferredItem<PickaxeToolItem> HAMMER = registerItem("hammer", () -> new PickaxeToolItem(ToolTypeSetup.HAMMER, new MaterialTier(), new Item.Properties().durability(1)));
    public static final DeferredItem<SwordToolItem> SWORD = registerItem("sword", () -> new SwordToolItem(ToolTypeSetup.SWORD, new MaterialTier(), new Item.Properties().durability(1)));

    public static final DeferredItem<PartShape> TOOL_SHAPE = registerItem("tool_shape", () -> new PartShape(new Item.Properties()));
    public static final DeferredItem<PartShape> BLADE_SHAPE = registerItem("blade_shape", () -> new PartShape(new Item.Properties()));
    public static final DeferredItem<PartShape> MISC_SHAPE = registerItem("misc_shape", () -> new PartShape(new Item.Properties()));


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

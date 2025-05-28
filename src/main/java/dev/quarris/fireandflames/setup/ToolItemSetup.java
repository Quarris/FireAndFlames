package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.tool.MaterialTier;
import dev.quarris.fireandflames.data.tool.ToolData;
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

    public static final DeferredItem<Item> PICKAXE_HEAD = registerItem("pickaxe_head", () -> new PartItem(PartTypeSetup.PICKAXE_HEAD, new Item.Properties()));
    public static final DeferredItem<Item> AXE_HEAD = registerItem("axe_head", () -> new PartItem(PartTypeSetup.AXE_HEAD, new Item.Properties()));
    public static final DeferredItem<Item> SHOVEL_HEAD = registerItem("shovel_head", () -> new PartItem(PartTypeSetup.SHOVEL_HEAD, new Item.Properties()));
    public static final DeferredItem<Item> HOE_HEAD = registerItem("hoe_head", () -> new PartItem(PartTypeSetup.HOE_HEAD, new Item.Properties()));
    public static final DeferredItem<Item> SWORD_BLADE = registerItem("sword_blade", () -> new PartItem(PartTypeSetup.SWORD_BLADE, new Item.Properties()));

    public static final DeferredItem<Item> BINDING = registerItem("binding", () -> new PartItem(PartTypeSetup.BINDING, new Item.Properties()));
    public static final DeferredItem<Item> HANDLE = registerItem("handle", () -> new PartItem(PartTypeSetup.HANDLE, new Item.Properties()));
    public static final DeferredItem<Item> WIDE_GUARD = registerItem("wide_guard", () -> new PartItem(PartTypeSetup.WIDE_GUARD, new Item.Properties()));


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

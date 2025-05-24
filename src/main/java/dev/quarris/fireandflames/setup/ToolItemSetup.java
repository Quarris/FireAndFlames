package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.tool.MaterialTier;
import dev.quarris.fireandflames.data.tool.ToolData;
import dev.quarris.fireandflames.world.item.tool.AxeToolItem;
import dev.quarris.fireandflames.world.item.tool.PartItem;
import dev.quarris.fireandflames.world.item.tool.PickaxeToolItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public class ToolItemSetup {

    public static final DeferredRegister.Items REGISTRY = DeferredRegister.Items.createItems(ModRef.ID);

    public static final DeferredItem<PickaxeToolItem> PICKAXE = registerItem("pickaxe", () -> new PickaxeToolItem(ToolTypeSetup.PICKAXE, new MaterialTier(), new Item.Properties().component(DataComponentSetup.TOOL_DATA, ToolData.EMPTY).stacksTo(1).durability(1000)));
    public static final DeferredItem<AxeToolItem> AXE = registerItem("axe", () -> new AxeToolItem(new MaterialTier(), new Item.Properties().component(DataComponentSetup.TOOL_DATA, ToolData.EMPTY).stacksTo(1).durability(1000)));
    public static final DeferredItem<PickaxeToolItem> HAMMER = registerItem("hammer", () -> new PickaxeToolItem(ToolTypeSetup.HAMMER, new MaterialTier(), new Item.Properties().component(DataComponentSetup.TOOL_DATA, ToolData.EMPTY).stacksTo(1).durability(1000)));

    public static final DeferredItem<Item> PICKAXE_HEAD = registerItem("pickaxe_head", () -> new PartItem(PartTypeSetup.PICKAXE_HEAD, new Item.Properties().component(DataComponentSetup.TOOL_PART, null)));
    public static final DeferredItem<Item> AXE_HEAD = registerItem("axe_head", () -> new PartItem(PartTypeSetup.AXE_HEAD, new Item.Properties().component(DataComponentSetup.TOOL_PART, null)));
    public static final DeferredItem<Item> BINDING = registerItem("binding", () -> new PartItem(PartTypeSetup.BINDING, new Item.Properties().component(DataComponentSetup.TOOL_PART, null)));
    public static final DeferredItem<Item> HANDLE = registerItem("handle", () -> new PartItem(PartTypeSetup.HANDLE, new Item.Properties().component(DataComponentSetup.TOOL_PART, null)));

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

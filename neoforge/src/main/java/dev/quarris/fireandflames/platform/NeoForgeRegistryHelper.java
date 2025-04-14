package dev.quarris.fireandflames.platform;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.platform.services.IRegistryHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class NeoForgeRegistryHelper implements IRegistryHelper {

    // Using DeferredRegister for all the registry types
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.Blocks.createBlocks(ModRef.ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.Items.createItems(ModRef.ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ModRef.ID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ModRef.ID);

    // Map to keep track of registry objects for lazy lookup
    private static final Map<String, DeferredHolder<?, ?>> REGISTRY_OBJECTS = new HashMap<>();

    public static void init(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        MENUS.register(modEventBus);
    }

    @Override
    public <T extends Block> Supplier<T> registerBlock(String name, Supplier<T> blockSupplier, Item.Properties properties) {
        Supplier<T> block = registerBlockNoItem(name, blockSupplier);
        registerItem(name, () -> new BlockItem(block.get(), properties));
        return block;
    }

    @Override
    public <T extends Block> Supplier<T> registerBlockNoItem(String name, Supplier<T> blockSupplier) {
        DeferredHolder<Block, T> registryObject = BLOCKS.register(name, blockSupplier);
        REGISTRY_OBJECTS.put("block:" + name, registryObject);
        return registryObject;
    }

    @Override
    public <T extends Item> Supplier<T> registerItem(String name, Supplier<T> itemSupplier) {
        DeferredHolder<Item, T> registryObject = ITEMS.register(name, itemSupplier);
        REGISTRY_OBJECTS.put("item:" + name, registryObject);
        return registryObject;
    }

    @Override
    public <T extends BlockEntityType<E>, E extends BlockEntity> Supplier<T> registerBlockEntity(String name, Supplier<T> blockEntityTypeSupplier) {
        DeferredHolder<BlockEntityType<?>, T> registryObject = BLOCK_ENTITIES.register(name, blockEntityTypeSupplier);
        REGISTRY_OBJECTS.put("block_entity:" + name, registryObject);
        return registryObject;
    }

    @Override
    public <T extends MenuType<E>, E extends AbstractContainerMenu> Supplier<T> registerMenuType(String name, Supplier<T> menuTypeSupplier) {
        DeferredHolder<MenuType<?>, T> registryObject = MENUS.register(name, menuTypeSupplier);
        REGISTRY_OBJECTS.put("menu:" + name, registryObject);
        return registryObject;
    }
}
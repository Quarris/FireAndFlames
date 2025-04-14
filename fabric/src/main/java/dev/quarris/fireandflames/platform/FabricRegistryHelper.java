package dev.quarris.fireandflames.platform;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.platform.services.IRegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class FabricRegistryHelper implements IRegistryHelper {

    @Override
    public <T extends Block> Supplier<T> registerBlock(String name, Supplier<T> blockSupplier, Item.Properties properties) {
        T block = registerBlockNoItem(name, blockSupplier).get();
        registerItem(name, () -> new BlockItem(block, properties));
        return () -> block;
    }

    @Override
    public <T extends Block> Supplier<T> registerBlockNoItem(String name, Supplier<T> blockSupplier) {
        ResourceLocation id = ModRef.res(name);
        T block = blockSupplier.get();
        Registry.register(BuiltInRegistries.BLOCK, id, block);
        return () -> block;
    }

    @Override
    public <T extends Item> Supplier<T> registerItem(String name, Supplier<T> itemSupplier) {
        ResourceLocation id = ModRef.res(name);
        T item = itemSupplier.get();
        Registry.register(BuiltInRegistries.ITEM, id, item);
        return () -> item;
    }

    @Override
    public <T extends BlockEntityType<E>, E extends BlockEntity> Supplier<T> registerBlockEntity(String name, Supplier<T> blockEntityTypeSupplier) {
        ResourceLocation id = ModRef.res(name);
        T blockEntityType = blockEntityTypeSupplier.get();
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, blockEntityType);
        return () -> blockEntityType;
    }

    @Override
    public <T extends MenuType<E>, E extends AbstractContainerMenu> Supplier<T> registerMenuType(String name, Supplier<T> menuTypeSupplier) {
        ResourceLocation id = ModRef.res(name);
        T menuType = menuTypeSupplier.get();
        Registry.register(BuiltInRegistries.MENU, id, menuType);
        return () -> menuType;
    }
}
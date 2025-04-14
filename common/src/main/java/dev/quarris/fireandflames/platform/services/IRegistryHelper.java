package dev.quarris.fireandflames.platform.services;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

/**
 * Registry helper service interface for platform-agnostic registry operations
 */
public interface IRegistryHelper {

    /**
     * Creates a block and automatically registers a BlockItem for it
     *
     * @param name The registry name without the mod id
     * @param blockSupplier The block supplier
     * @param properties The item properties for the BlockItem
     * @return A supplier for the registered block
     */
    <T extends Block> Supplier<T> registerBlock(String name, Supplier<T> blockSupplier, Item.Properties properties);

    /**
     * Creates a block without a BlockItem
     *
     * @param name The registry name without the mod id
     * @param blockSupplier The block supplier
     * @return A supplier for the registered block
     */
    <T extends Block> Supplier<T> registerBlockNoItem(String name, Supplier<T> blockSupplier);

    /**
     * Registers an item
     *
     * @param name The registry name without the mod id
     * @param itemSupplier The item supplier
     * @return A supplier for the registered item
     */
    <T extends Item> Supplier<T> registerItem(String name, Supplier<T> itemSupplier);

    /**
     * Registers a block entity type
     *
     * @param name The registry name without the mod id
     * @param blockEntityTypeSupplier The block entity type supplier
     * @return A supplier for the registered block entity type
     */
    <T extends BlockEntityType<E>, E extends BlockEntity> Supplier<T> registerBlockEntity(String name, Supplier<T> blockEntityTypeSupplier);

    /**
     * Registers a menu type
     *
     * @param name The registry name without the mod id
     * @param menuTypeSupplier The menu type supplier
     * @return A supplier for the registered menu type
     */
    <T extends MenuType<E>, E extends AbstractContainerMenu> Supplier<T> registerMenuType(String name, Supplier<T> menuTypeSupplier);
}
package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.platform.Services;
import dev.quarris.fireandflames.platform.services.IRegistryHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class BlockEntitySetup {
    private static final IRegistryHelper REGISTRY = Services.REGISTRY;
    private static <T extends BlockEntityType<E>, E extends BlockEntity> Supplier<T> registerBlockEntity(String name, Supplier<T> blockEntityTypeSupplier) {
        return REGISTRY.registerBlockEntity(name, blockEntityTypeSupplier);
    }
    public static void init() {}
}

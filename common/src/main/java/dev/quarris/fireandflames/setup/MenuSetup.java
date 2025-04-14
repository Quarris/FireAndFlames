package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.platform.Services;
import dev.quarris.fireandflames.platform.services.IRegistryHelper;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class MenuSetup {
    private static final IRegistryHelper REGISTRY = Services.REGISTRY;
    private static <T extends MenuType<E>, E extends AbstractContainerMenu> Supplier<T> registerMenuType(String name, Supplier<T> menuTypeSupplier) {
        return REGISTRY.registerMenuType(name, menuTypeSupplier);
    }
    public static void init() {}
}

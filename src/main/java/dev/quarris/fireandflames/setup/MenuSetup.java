package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.world.inventory.menu.CrucibleMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MenuSetup {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, ModRef.ID);

    public static final Supplier<MenuType<CrucibleMenu>> CRUCIBLE = REGISTRY.register("crucible", () -> IMenuTypeExtension.create(CrucibleMenu::new));

    public static void init(IEventBus modBus) {
        REGISTRY.register(modBus);
    }
}

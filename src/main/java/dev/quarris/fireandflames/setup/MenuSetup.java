package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.world.inventory.menu.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MenuSetup {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, ModRef.ID);

    public static final Supplier<MenuType<CrucibleMenu>> CRUCIBLE = REGISTRY.register("crucible", () -> IMenuTypeExtension.create(CrucibleMenu::new));
    public static final Supplier<MenuType<CrucibleBurnerMenu>> CRUCIBLE_BURNER = REGISTRY.register("crucible_burner", () -> IMenuTypeExtension.create(CrucibleBurnerMenu::new));
    public static final Supplier<MenuType<TinkersWorkbenchMenu>> TINKERS_WORKBENCH = REGISTRY.register("tinkers_workbench", () -> new MenuType<>(TinkersWorkbenchMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final Supplier<MenuType<SmithingAnvilMenu>> SMITHING_ANVIL = REGISTRY.register("smithing_anvil", () -> new MenuType<>(SmithingAnvilMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final Supplier<MenuType<ArtisanTableMenu>> ARTISAN_TABLE = REGISTRY.register("artisan_table", () -> new MenuType<>(ArtisanTableMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static void init(IEventBus modBus) {
        REGISTRY.register(modBus);
    }
}

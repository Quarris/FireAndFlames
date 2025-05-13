package dev.quarris.fireandflames;


import dev.quarris.fireandflames.config.ServerConfigs;
import dev.quarris.fireandflames.setup.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(ModRef.ID)
public class FireAndFlames {

    public FireAndFlames(ModContainer container, IEventBus modBus) {
        container.registerConfig(ModConfig.Type.SERVER, ServerConfigs.SPEC);

        RegistrySetup.init(modBus);
        BlockSetup.init(modBus);
        ItemSetup.init(modBus);
        DataComponentSetup.init(modBus);
        FluidSetup.init(modBus);
        BlockEntitySetup.init(modBus);
        MenuSetup.init(modBus);
        RecipeSetup.init(modBus);
        CreativeTabSetup.init(modBus);
        NumberProviderSetup.init(modBus);
    }


}

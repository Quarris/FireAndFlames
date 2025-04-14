package dev.quarris.fireandflames;


import dev.quarris.fireandflames.platform.NeoForgeRegistryHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(ModRef.ID)
public class FireAndFlames {

    public FireAndFlames(ModContainer container, IEventBus eventBus) {
        // Initialize registry
        NeoForgeRegistryHelper.init(eventBus);
        CommonClass.init();
    }
}

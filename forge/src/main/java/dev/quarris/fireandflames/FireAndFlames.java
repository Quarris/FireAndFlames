package dev.quarris.fireandflames;

import dev.quarris.fireandflames.platform.ForgeRegistryHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModRef.ID)
public class FireAndFlames {

    public FireAndFlames() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Initialize registry
        ForgeRegistryHelper.init(modEventBus);
        CommonClass.init();

    }
}

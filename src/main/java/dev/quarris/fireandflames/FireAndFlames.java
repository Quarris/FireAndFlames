package dev.quarris.fireandflames;


import dev.quarris.fireandflames.setup.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(ModRef.ID)
public class FireAndFlames {

    public FireAndFlames(ModContainer container, IEventBus modBus) {
        BlockSetup.init(modBus);
        ItemSetup.init(modBus);
        BlockEntitySetup.init(modBus);
        MenuSetup.init(modBus);
        RecipeSetup.init(modBus);
        CreativeTabSetup.init(modBus);
    }
}

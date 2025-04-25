package dev.quarris.fireandflames;


import dev.quarris.fireandflames.setup.BlockEntitySetup;
import dev.quarris.fireandflames.setup.BlockSetup;
import dev.quarris.fireandflames.setup.ItemSetup;
import dev.quarris.fireandflames.setup.MenuSetup;
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
    }
}

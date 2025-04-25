package dev.quarris.fireandflames.client.event.handler;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.client.screen.CrucibleScreen;
import dev.quarris.fireandflames.setup.MenuSetup;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = ModRef.ID, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetupEvents {

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(MenuSetup.CRUCIBLE.get(), CrucibleScreen::new);
    }
}

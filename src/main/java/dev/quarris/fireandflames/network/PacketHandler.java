package dev.quarris.fireandflames.network;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.network.payload.CrucibleScrollC2SPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = ModRef.ID, bus = EventBusSubscriber.Bus.MOD)
public class PacketHandler {

    @SubscribeEvent
    private static void registerPayload(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");

        registrar.playToServer(CrucibleScrollC2SPayload.TYPE, CrucibleScrollC2SPayload.CODEC, CrucibleScrollC2SPayload::handle);
    }
}

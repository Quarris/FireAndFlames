package dev.quarris.fireandflames.network;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.network.payload.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = ModRef.ID, bus = EventBusSubscriber.Bus.MOD)
public class PacketHandler {

    @SubscribeEvent
    private static void registerPayload(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");

        // Client to Server
        registrar.playToServer(CrucibleScrollC2SPayload.TYPE, CrucibleScrollC2SPayload.CODEC, CrucibleScrollC2SPayload::handle);
        registrar.playToServer(TinkersWorkbenchToolNameChangeC2SPayload.TYPE, TinkersWorkbenchToolNameChangeC2SPayload.CODEC, TinkersWorkbenchToolNameChangeC2SPayload::handle);
        registrar.playToServer(TinkersWorkbenchChangeTabC2SPayload.TYPE, TinkersWorkbenchChangeTabC2SPayload.CODEC, TinkersWorkbenchChangeTabC2SPayload::handle);
        registrar.playToServer(SmithingAnvilHammerPayload.TYPE, SmithingAnvilHammerPayload.CODEC, SmithingAnvilHammerPayload::handle);
        registrar.playToServer(ArtisanTableSetOutputSelectionS2CPayload.TYPE, ArtisanTableSetOutputSelectionS2CPayload.CODEC, ArtisanTableSetOutputSelectionS2CPayload::handle);

        // Server to Client
        registrar.playToClient(ArtisanTableSetOutputsS2CPayload.TYPE, ArtisanTableSetOutputsS2CPayload.CODEC, ArtisanTableSetOutputsS2CPayload::handle);
        registrar.playToClient(MaterialConverterDataPayload.TYPE, MaterialConverterDataPayload.CODEC, MaterialConverterDataPayload::handle);
    }
}

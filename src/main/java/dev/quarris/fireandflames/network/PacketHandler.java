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
        registrar.playToServer(SBCrucibleScroll.TYPE, SBCrucibleScroll.CODEC, SBCrucibleScroll::handle);
        registrar.playToServer(SBTinkersWorkbenchToolNameChange.TYPE, SBTinkersWorkbenchToolNameChange.CODEC, SBTinkersWorkbenchToolNameChange::handle);
        registrar.playToServer(SBTinkersWorkbenchChangeTab.TYPE, SBTinkersWorkbenchChangeTab.CODEC, SBTinkersWorkbenchChangeTab::handle);
        registrar.playToServer(SBSmithingAnvilHammer.TYPE, SBSmithingAnvilHammer.CODEC, SBSmithingAnvilHammer::handle);
        registrar.playToServer(CBArtisanTableSetOutputSelection.TYPE, CBArtisanTableSetOutputSelection.CODEC, CBArtisanTableSetOutputSelection::handle);

        // Server to Client
        registrar.playToClient(CBArtisanTableSetOutputs.TYPE, CBArtisanTableSetOutputs.CODEC, CBArtisanTableSetOutputs::handle);
        registrar.playToClient(CBMaterialConverterData.TYPE, CBMaterialConverterData.CODEC, CBMaterialConverterData::handle);
        registrar.playToClient(CBUpdateToolModifiers.TYPE, CBUpdateToolModifiers.CODEC, CBUpdateToolModifiers::handle);
    }
}

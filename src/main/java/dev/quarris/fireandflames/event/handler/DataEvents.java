package dev.quarris.fireandflames.event.handler;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.network.payload.CBUpdateToolModifiers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = ModRef.ID)
public class DataEvents {

    @SubscribeEvent
    private static void syncData(OnDatapackSyncEvent event) {
        CBUpdateToolModifiers toolModifierPayload = new CBUpdateToolModifiers(ModRef.DATA_MANAGER.getToolModifiers().getAllModifiers());
        event.getRelevantPlayers().forEach(player -> PacketDistributor.sendToPlayer(player, toolModifierPayload));
    }

}

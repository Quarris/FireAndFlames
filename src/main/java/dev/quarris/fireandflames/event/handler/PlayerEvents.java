package dev.quarris.fireandflames.event.handler;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.network.payload.MaterialConverterDataPayload;
import dev.quarris.fireandflames.util.data.DataMapUtil;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = ModRef.ID)
public class PlayerEvents {

    @SubscribeEvent
    public static void syncPlayerData(OnDatapackSyncEvent event) {
        var payload = new MaterialConverterDataPayload(DataMapUtil.getAllConverters(event.getPlayerList().getServer().registryAccess()));
        event.getRelevantPlayers().forEach(player -> PacketDistributor.sendToPlayer(player, payload));
    }
}

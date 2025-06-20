package dev.quarris.fireandflames.event.handler;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.network.payload.MaterialConverterDataPayload;
import dev.quarris.fireandflames.util.data.DataMapUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = ModRef.ID)
public class PlayerEvents {

    @SubscribeEvent
    public static void syncPlayerData(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            HolderLookup.Provider registries = player.registryAccess();
            PacketDistributor.sendToPlayer(player, new MaterialConverterDataPayload(DataMapUtil.getAllConverters(registries)));
        }
    }
}

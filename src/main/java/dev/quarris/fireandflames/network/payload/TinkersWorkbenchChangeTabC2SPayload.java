package dev.quarris.fireandflames.network.payload;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.world.inventory.menu.TinkersWorkbenchMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TinkersWorkbenchChangeTabC2SPayload(ResourceLocation tab) implements CustomPacketPayload {

    public static final ResourceLocation ID = ModRef.res("tinkers_workbench_change_tab");
    public static final Type<TinkersWorkbenchChangeTabC2SPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, TinkersWorkbenchChangeTabC2SPayload> CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC, TinkersWorkbenchChangeTabC2SPayload::tab,
        TinkersWorkbenchChangeTabC2SPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TinkersWorkbenchChangeTabC2SPayload payload, IPayloadContext ctx) {
        if (ctx.player().containerMenu instanceof TinkersWorkbenchMenu menu) {
            menu.setTabByName(payload.tab);
        }
    }
}

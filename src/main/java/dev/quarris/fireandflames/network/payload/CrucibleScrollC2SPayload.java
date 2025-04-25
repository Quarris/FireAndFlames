package dev.quarris.fireandflames.network.payload;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.world.inventory.menu.CrucibleMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CrucibleScrollC2SPayload(int scroll) implements CustomPacketPayload {

    public static final ResourceLocation ID = ModRef.res("crucible_scroll");
    public static final Type<CrucibleScrollC2SPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, CrucibleScrollC2SPayload> CODEC = StreamCodec.composite(ByteBufCodecs.INT, CrucibleScrollC2SPayload::scroll, CrucibleScrollC2SPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CrucibleScrollC2SPayload payload, IPayloadContext ctx) {
        if (ctx.player().containerMenu instanceof CrucibleMenu menu) {
            menu.scrollTo(payload.scroll);
        }
    }
}

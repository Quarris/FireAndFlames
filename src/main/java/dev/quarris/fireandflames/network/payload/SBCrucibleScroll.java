package dev.quarris.fireandflames.network.payload;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.world.inventory.menu.CrucibleMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SBCrucibleScroll(int scroll) implements CustomPacketPayload {

    public static final ResourceLocation ID = ModRef.res("crucible_scroll");
    public static final Type<SBCrucibleScroll> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SBCrucibleScroll> CODEC = StreamCodec.composite(ByteBufCodecs.INT, SBCrucibleScroll::scroll, SBCrucibleScroll::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SBCrucibleScroll payload, IPayloadContext ctx) {
        if (ctx.player().containerMenu instanceof CrucibleMenu menu) {
            menu.scrollTo(payload.scroll);
        }
    }
}

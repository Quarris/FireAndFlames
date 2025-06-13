package dev.quarris.fireandflames.network.payload;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.world.inventory.menu.SmithingAnvilMenu;
import dev.quarris.fireandflames.world.inventory.menu.TinkersWorkbenchMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SmithingAnvilHammerPayload(int index) implements CustomPacketPayload {

    public static final ResourceLocation ID = ModRef.res("smithing_anvil_hammer");
    public static final CustomPacketPayload.Type<SmithingAnvilHammerPayload> TYPE = new CustomPacketPayload.Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SmithingAnvilHammerPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, SmithingAnvilHammerPayload::index,
        SmithingAnvilHammerPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SmithingAnvilHammerPayload payload, IPayloadContext ctx) {
        if (ctx.player().containerMenu instanceof SmithingAnvilMenu menu) {
            menu.onHammerHit(payload.index);
        }
    }

}

package dev.quarris.fireandflames.network.payload;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.world.inventory.menu.TinkersWorkbenchMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TinkersWorkbenchToolNameChangeC2SPayload(String toolName) implements CustomPacketPayload {

    public static final ResourceLocation ID = ModRef.res("tinkers_workbench_tool_name_chane");
    public static final Type<TinkersWorkbenchToolNameChangeC2SPayload> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, TinkersWorkbenchToolNameChangeC2SPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, TinkersWorkbenchToolNameChangeC2SPayload::toolName,
        TinkersWorkbenchToolNameChangeC2SPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TinkersWorkbenchToolNameChangeC2SPayload payload, IPayloadContext ctx) {
        if (ctx.player().containerMenu instanceof TinkersWorkbenchMenu menu) {
            menu.setToolName(payload.toolName);
        }
    }
}

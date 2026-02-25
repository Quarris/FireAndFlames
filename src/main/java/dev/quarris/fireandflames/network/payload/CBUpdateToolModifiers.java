package dev.quarris.fireandflames.network.payload;

import com.google.common.collect.ImmutableMap;
import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.tool.modifier.ToolModifier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Map;

public record CBUpdateToolModifiers(Map<ResourceLocation, ToolModifier> modifiers) implements CustomPacketPayload {

    public static final ResourceLocation ID = ModRef.res("update_tool_modifiers");
    public static final Type<CBUpdateToolModifiers> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, CBUpdateToolModifiers> CODEC = StreamCodec.of(CBUpdateToolModifiers::encode, CBUpdateToolModifiers::decode);

    @Override
    public Type<CBUpdateToolModifiers> type() {
        return TYPE;
    }

    private static void encode(RegistryFriendlyByteBuf buf, CBUpdateToolModifiers payload) {
        buf.writeVarInt(payload.modifiers().size());
        payload.modifiers().forEach((id, modifier) -> {
            buf.writeUtf(id.toString());
            ToolModifier.STREAM_CODEC.encode(buf, modifier);
        });
    }

    private static CBUpdateToolModifiers decode(RegistryFriendlyByteBuf buf) {
        ImmutableMap.Builder<ResourceLocation, ToolModifier> mapBuilder = new ImmutableMap.Builder<>();
        int size = buf.readVarInt();
        for (int i = 0; i < size; i++) {
            ResourceLocation id = ResourceLocation.parse(buf.readUtf());
            ToolModifier modifier = ToolModifier.STREAM_CODEC.decode(buf);
            mapBuilder.put(id, modifier);
        }

        return new CBUpdateToolModifiers(mapBuilder.build());
    }

    public static void handle(CBUpdateToolModifiers payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ModRef.DATA_MANAGER.getToolModifiers().replaceModifiers(payload.modifiers());
        });
    }
}

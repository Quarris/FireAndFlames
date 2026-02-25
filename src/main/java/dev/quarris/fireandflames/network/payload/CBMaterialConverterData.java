package dev.quarris.fireandflames.network.payload;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.client.data.ClientMaterialConverters;
import dev.quarris.fireandflames.data.map.ConverterData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record CBMaterialConverterData(List<ConverterData> converterData) implements CustomPacketPayload {

    public static final ResourceLocation ID = ModRef.res("material_converter_data");
    public static final Type<CBMaterialConverterData> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, CBMaterialConverterData> CODEC = StreamCodec.composite(
        ConverterData.LIST_STREAM_CODEC, CBMaterialConverterData::converterData,
        CBMaterialConverterData::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(CBMaterialConverterData payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ClientMaterialConverters.resetConverters();
            ClientMaterialConverters.addConverters(payload.converterData);
        });
    }
}

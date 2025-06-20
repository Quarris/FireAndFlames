package dev.quarris.fireandflames.data.map;

import dev.quarris.fireandflames.data.tool.material.ToolMaterial;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record ConverterData(Holder<ToolMaterial> material, IMaterialConverter<?> converter) {

    public static final StreamCodec<RegistryFriendlyByteBuf, ConverterData> STREAM_CODEC = StreamCodec.composite(
        ToolMaterial.HOLDER_STREAM_CODEC, ConverterData::material,
        IMaterialConverter.STREAM_CODEC, ConverterData::converter,
        ConverterData::new
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, List<ConverterData>> LIST_STREAM_CODEC = STREAM_CODEC.apply(ByteBufCodecs.list());

}

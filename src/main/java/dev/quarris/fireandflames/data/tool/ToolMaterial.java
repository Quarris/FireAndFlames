package dev.quarris.fireandflames.data.tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ToolMaterial(String name) {

    public static final Codec<ToolMaterial> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("name").forGetter(ToolMaterial::name)
    ).apply(instance, ToolMaterial::new));

    public static final Codec<ToolMaterial> NETWORK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("name").forGetter(ToolMaterial::name)
    ).apply(instance, ToolMaterial::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToolMaterial> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, ToolMaterial::name,
        ToolMaterial::new
    );
}

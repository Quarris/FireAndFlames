package dev.quarris.fireandflames.data.tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.quarris.fireandflames.data.tool.part.ToolParts;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ToolData(ToolParts toolParts) {

    public static final ToolData EMPTY = new ToolData(ToolParts.EMPTY);

    public static final Codec<ToolData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ToolParts.CODEC.fieldOf("tool_parts").forGetter(ToolData::toolParts)
    ).apply(instance, ToolData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToolData> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    public boolean isEmpty() {
        return this == EMPTY || this.toolParts.isEmpty();
    }


}

package dev.quarris.fireandflames.data.tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.quarris.fireandflames.data.tool.modifier.ToolModifier;
import dev.quarris.fireandflames.data.tool.part.ToolParts;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Collections;
import java.util.List;

public record ToolData(ToolParts toolParts, List<ToolModifier> modifiers) {

    public static final ToolData EMPTY = new ToolData(ToolParts.EMPTY, Collections.emptyList());

    public static final Codec<ToolData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ToolParts.CODEC.fieldOf("tool_parts").forGetter(ToolData::toolParts),
        ToolModifier.CODEC.listOf().fieldOf("tool_modifier").forGetter(ToolData::modifiers)
    ).apply(instance, ToolData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToolData> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC);

    public boolean isEmpty() {
        return this == EMPTY || this.toolParts.isEmpty();
    }


}

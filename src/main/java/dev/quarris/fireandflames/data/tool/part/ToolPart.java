package dev.quarris.fireandflames.data.tool.part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.quarris.fireandflames.data.tool.material.ToolMaterial;
import dev.quarris.fireandflames.setup.DataComponentSetup;
import dev.quarris.fireandflames.setup.RegistrySetup;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record ToolPart(Holder<ToolMaterial> material) {

    public static final ToolPart EMPTY = new ToolPart(null);

    public static final Codec<ToolPart> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ToolMaterial.HOLDER_CODEC.fieldOf("material").forGetter(ToolPart::material)
    ).apply(instance, ToolPart::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToolPart> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.holderRegistry(RegistrySetup.Keys.MATERIALS), ToolPart::material,
        ToolPart::new
    );

    @Override
    public String toString() {
        return "ToolPart{" +
            ", material=" + material +
            '}';
    }

    public boolean isEmpty() {
        return this == EMPTY || this.material == null;
    }

    public static Optional<ToolPart> fromStack(ItemStack stack) {
        ToolPart toolPart = stack.get(DataComponentSetup.TOOL_PART);

        if (toolPart != null) {
            return Optional.of(toolPart);
        }

        return Optional.empty();
    }
}

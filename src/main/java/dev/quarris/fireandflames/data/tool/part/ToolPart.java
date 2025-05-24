package dev.quarris.fireandflames.data.tool.part;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import dev.quarris.fireandflames.data.tool.ToolMaterial;
import dev.quarris.fireandflames.setup.DataComponentSetup;
import dev.quarris.fireandflames.setup.RegistrySetup;
import dev.quarris.fireandflames.setup.TagSetup;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record ToolPart(Holder<PartType> type, ToolMaterial material) {

    public static final Codec<ToolPart> CODEC = Codec.pair(
        RegistrySetup.PART_TYPES.holderByNameCodec().fieldOf("type").codec(),
        ToolMaterial.CODEC.fieldOf("material").codec()
    ).xmap(pair -> new ToolPart(pair.getFirst(), pair.getSecond()), part -> Pair.of(part.type(), part.material()));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToolPart> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.holderRegistry(RegistrySetup.Keys.PART_TYPES), ToolPart::type,
        ToolMaterial.STREAM_CODEC, ToolPart::material,
        ToolPart::new
    );

    @Override
    public String toString() {
        return "ToolPart{" +
            "type=" + type +
            ", material=" + material +
            '}';
    }

    public static Optional<ToolPart> fromStack(ItemStack stack) {
        ToolPart toolPart = stack.get(DataComponentSetup.TOOL_PART);

        if (toolPart != null) {
            return Optional.of(toolPart);
        }

        return Optional.empty();
    }
}

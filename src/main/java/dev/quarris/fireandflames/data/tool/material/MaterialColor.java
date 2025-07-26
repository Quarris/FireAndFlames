package dev.quarris.fireandflames.data.tool.material;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.FastColor;

import java.util.List;

public record MaterialColor(int r, int g, int b) {

    public MaterialColor(List<Integer> rgb) {
        this(rgb.get(0), rgb.get(1), rgb.get(2));
    }

    public List<Integer> rgb() {
        return List.of(r, g, b);
    }

    public int packed() {
        return FastColor.ARGB32.color(r, g, b);
    }

    public static final Codec<MaterialColor> CODEC = Codec.INT.listOf(3, 3).xmap(MaterialColor::new, MaterialColor::rgb);

    public static final StreamCodec<RegistryFriendlyByteBuf, MaterialColor> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT.apply(ByteBufCodecs.list(3)), MaterialColor::rgb,
        MaterialColor::new
    );
}

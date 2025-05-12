package dev.quarris.fireandflames.data.maps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record FuelData(int heat, int burnTicks) {
    public static final Codec<FuelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("heat").forGetter(FuelData::heat),
        Codec.INT.fieldOf("burn_ticks").forGetter(FuelData::burnTicks)
    ).apply(instance, FuelData::new));
}

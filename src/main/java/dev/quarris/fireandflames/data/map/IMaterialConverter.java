package dev.quarris.fireandflames.data.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.quarris.fireandflames.setup.RegistrySetup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public interface IMaterialConverter<T> {

    Codec<IMaterialConverter<?>> CODEC = RegistrySetup.MATERIAL_CONVERTERS.byNameCodec().dispatch(IMaterialConverter::codec, Function.identity());

    StreamCodec<RegistryFriendlyByteBuf, IMaterialConverter<?>> STREAM_CODEC = ByteBufCodecs.registry(RegistrySetup.Keys.MATERIAL_CONVERTERS)
        .dispatch(IMaterialConverter::codec, c -> ByteBufCodecs.fromCodecWithRegistries(c.codec()));

    MapCodec<? extends IMaterialConverter<T>> codec();

    boolean matches(Object input);

    int getCountForUnits(int requiredUnits);

    int getUnits();

}

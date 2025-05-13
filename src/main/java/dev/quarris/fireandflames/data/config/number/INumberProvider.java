package dev.quarris.fireandflames.data.config.number;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.quarris.fireandflames.setup.RegistrySetup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public interface INumberProvider {

    Codec<Either<Double, INumberProvider>> CONSTANT_OR_DISPATCH_CODEC = Codec.either(
        Codec.DOUBLE,
        RegistrySetup.NUMBER_PROVIDERS.byNameCodec().dispatch(INumberProvider::codec, Function.identity())
    );

    Codec<INumberProvider> CODEC = CONSTANT_OR_DISPATCH_CODEC.xmap(
        left -> left.map(ConstantNumber::new, Function.identity()),
        right -> right instanceof ConstantNumber ? Either.left(right.evaluate()) : Either.right(right)
    );

    StreamCodec<RegistryFriendlyByteBuf, INumberProvider> STREAM_CODEC = ByteBufCodecs.registry(RegistrySetup.Keys.NUMBER_PROVIDERS)
        .dispatch(INumberProvider::codec, c -> ByteBufCodecs.fromCodecWithRegistries(c.codec()));

    double evaluate();

    default int evaluateInt() {
        return ((int) this.evaluate());
    }

    MapCodec<? extends INumberProvider> codec();
}

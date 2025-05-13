package dev.quarris.fireandflames.data.config.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.quarris.fireandflames.setup.NumberProviderSetup;

public record ConstantNumber(double value) implements INumberProvider {

    public static final MapCodec<ConstantNumber> CODEC = Codec.DOUBLE.xmap(ConstantNumber::new, ConstantNumber::evaluate).fieldOf("value");

    @Override
    public double evaluate() {
        return this.value;
    }

    @Override
    public MapCodec<ConstantNumber> codec() {
        return NumberProviderSetup.CONSTANT.get();
    }
}

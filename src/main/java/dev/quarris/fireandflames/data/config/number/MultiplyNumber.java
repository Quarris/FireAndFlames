package dev.quarris.fireandflames.data.config.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.quarris.fireandflames.setup.NumberProviderSetup;

import java.util.Arrays;
import java.util.List;

public record MultiplyNumber(List<INumberProvider> values) implements INumberProvider {

    public MultiplyNumber(INumberProvider... values) {
        this(Arrays.asList(values));
    }

    public MultiplyNumber(double... values) {
        this(Arrays.stream(values).mapToObj(value -> (INumberProvider) new ConstantNumber(value)).toList());
    }

    public static final MapCodec<MultiplyNumber> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        INumberProvider.CODEC.listOf().fieldOf("values").forGetter(MultiplyNumber::values)
    ).apply(instance, MultiplyNumber::new));

    @Override
    public double evaluate() {
        double result = 1;
        for (INumberProvider provider : this.values) {
            result *= provider.evaluate();
        }
        return result;
    }

    @Override
    public MapCodec<MultiplyNumber> codec() {
        return NumberProviderSetup.MULTIPLY.get();
    }
}

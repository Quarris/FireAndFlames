package dev.quarris.fireandflames.data.config.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.quarris.fireandflames.setup.NumberProviderSetup;

import java.util.Arrays;
import java.util.List;

public record AddNumber(List<INumberProvider> values) implements INumberProvider {

    public AddNumber(INumberProvider... values) {
        this(Arrays.asList(values));
    }

    public AddNumber(double... values) {
        this(Arrays.stream(values).mapToObj(value -> (INumberProvider) new ConstantNumber(value)).toList());
    }

    public static final MapCodec<AddNumber> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        INumberProvider.CODEC.listOf().fieldOf("values").forGetter(AddNumber::values)
    ).apply(instance, AddNumber::new));

    @Override
    public double evaluate() {
        double result = 0;
        for (INumberProvider provider : this.values) {
            result += provider.evaluate();
        }
        return result;
    }

    @Override
    public MapCodec<AddNumber> codec() {
        return NumberProviderSetup.ADD.get();
    }
}

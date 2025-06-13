package dev.quarris.fireandflames.data.map;

import com.mojang.serialization.Codec;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"rawtypes"})
public record MaterialConversion(List<IMaterialConverter<?>> converters) {

    public static final Codec<MaterialConversion> CODEC = IMaterialConverter.CODEC.listOf().xmap(MaterialConversion::new, MaterialConversion::converters);

    public List<IMaterialConverter<?>> getMatchingConverters(Object input) {
        List<IMaterialConverter<?>> matchingConverters = new ArrayList<>();
        for (IMaterialConverter converter : this.converters) {
            if (converter.matches(input)) {
                matchingConverters.add(converter);
            }
        }

        return matchingConverters;
    }

    public boolean matches(Object input) {
        for (IMaterialConverter converter : converters) {
            if (converter.matches(input)) {
                return true;
            }
        }

        return false;
    }

    public int getCountForUnits(Object input, int units) {
        for (IMaterialConverter converter : converters) {
            if (converter.matches(input)) {
                return converter.getCountForUnits(units);
            }
        }

        return -1;
    }
}

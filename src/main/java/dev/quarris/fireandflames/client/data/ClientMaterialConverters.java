package dev.quarris.fireandflames.client.data;

import dev.quarris.fireandflames.data.map.ConverterData;

import java.util.ArrayList;
import java.util.List;

public class ClientMaterialConverters {

    private static final List<ConverterData> CONVERTERS = new ArrayList<>();

    public static void addConverters(List<ConverterData> data) {
        CONVERTERS.addAll(data);
    }

    public static void resetConverters() {
        CONVERTERS.clear();
    }

    public static List<ConverterData> getConverters() {
        return new ArrayList<>(CONVERTERS);
    }
}

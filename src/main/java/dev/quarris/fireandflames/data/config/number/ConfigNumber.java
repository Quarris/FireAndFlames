package dev.quarris.fireandflames.data.config.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.quarris.fireandflames.config.ServerConfigs;
import dev.quarris.fireandflames.setup.NumberProviderSetup;

import java.util.function.Supplier;

public class ConfigNumber implements INumberProvider {

    public static final MapCodec<ConfigNumber> CODEC = Codec.STRING.xmap(ConfigNumber::new, provider -> provider.config.id).fieldOf("id");

    private final ConfigValue config;

    public ConfigNumber(String configId) {
        this.config = ConfigValue.byId(configId);
    }

    public ConfigNumber(ConfigValue config) {
        this.config = config;
    }

    @Override
    public double evaluate() {
        return this.config.value.get();
    }

    @Override
    public MapCodec<? extends INumberProvider> codec() {
        return NumberProviderSetup.CONFIG.get();
    }

    public enum ConfigValue {
        ORE_MULTIPLIER("ore_multiplier", ServerConfigs::getOreMultiplier),
        INGOT_MB("ingot_mb", ServerConfigs::getIngotMb),
        BLOCK_MB("block_mb", ServerConfigs::getBlockMb),
        NUGGET_MB("nugget_mb", ServerConfigs::getNuggetMb);

        private final String id;
        private final Supplier<Double> value;

        ConfigValue(String id, Supplier<Double> value) {
            this.id = id;
            this.value = value;
        }

        public double get() {
            return this.value.get();
        }

        public ConfigNumber toProvider() {
            return new ConfigNumber(this);
        }

        public static ConfigValue byId(String id) {
            for (ConfigValue value : values()) {
                if (value.id.equals(id)) {
                    return value;
                }
            }

            return null;
        }
    }
}

package dev.quarris.fireandflames.config;

import dev.quarris.fireandflames.ModRef;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = ModRef.ID, bus = EventBusSubscriber.Bus.MOD)
public class ServerConfigs {

    public static final ModConfigSpec SPEC;

    private static final ModConfigSpec.IntValue MAX_CRUCIBLE_SIZE;
    private static final ModConfigSpec.BooleanValue ENABLE_HEAT_REQUIREMENT;
    private static final ModConfigSpec.BooleanValue USE_BIOME_TEMPERATURE;
    private static final ModConfigSpec.IntValue BASE_TEMPERATURE;
    private static final ModConfigSpec.DoubleValue ULTRA_WARM_MODIFIER;
    private static final ModConfigSpec.BooleanValue USE_FLUID_TEMPERATURE;
    private static final ModConfigSpec.BooleanValue USE_ITEM_BURN_VALUE;
    private static final ModConfigSpec.IntValue ITEM_FUEL_HEAT;

    // Crucible
    private static int maxCrucibleSize;

    // Crucible.Heat
    private static boolean enableHeatRequirement;
    private static boolean useBiomeTemperature;
    private static int baseTemperature;
    private static double ultraWarmModifier;

    // Crucible.Fuel
    private static boolean useFluidTemperature;
    private static boolean useItemBurnValue;
    private static int itemFuelHeat;


    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment(
            " Crucible Configs"
        ).push("crucible");
        MAX_CRUCIBLE_SIZE = builder.comment(
            " Max (external) width/depth of the crucible."
        ).defineInRange("max_size", 23, 3, 100);

        builder.comment(
            " Heat Temperature Settings"
        ).push("heat");
        ENABLE_HEAT_REQUIREMENT = builder.comment(
            " Enables the heat requirement for recipes and fuels."
        ).define("enable_heat_requirement", true);

        USE_BIOME_TEMPERATURE = builder.comment(
            " Should the base temperature be based on the biome the crucible controller is in.",
            " Note: The biome temperatures are relatively arbitrary based on how Minecraft uses the values,",
            "       and the temperature is calculated based on (biome_temperature * base_temperature_config).",
            "       There is an extra config for heat adjustments for ultra warm dimensions (like The Nether)",
            " Example biome temperatures: Plains = 0.8, Snowy Plains = 0.0, Savannah/Badlands/AllNetherBiomes = 2.0, SnowyTaiga = -0.5"
        ).define("use_biome_temperature", true);

        BASE_TEMPERATURE = builder.comment(
            " ONLY WORKS WITH BIOME TEMPERATURES ARE ENABLED.",
            " The base temperature to use for the heat calculation (biome_temperature * base_temperature_config)"
        ).defineInRange("base_temperature", 400, Integer.MIN_VALUE, Integer.MAX_VALUE);

        ULTRA_WARM_MODIFIER = builder.comment(
            " ONLY WORKS WITH BIOME TEMPERATURES ARE ENABLED.",
            " The multiplier of ultra warm dimensions such as The Nether.",
            " The final temperature of the crucible without fuel for ultra warm dimensions is calculated as (biome_temperature * base_temperature_config * ultra_warm_modifier)",
            " Set to 1.0 to disable the modifier"
        ).defineInRange("ultra_warm_modifier", 2.0, -100, Double.MAX_VALUE);
        builder.pop();

        builder.comment(
            " Fuel Settings"
        ).push("fuel");
        USE_FLUID_TEMPERATURE = builder.comment(
            " Should fluid temperature be used as the base heat values if they are not defined in the datapack fuel maps",
            " The fluid temperature will be used as both heat and burn ticks."
        ).define("use_fluid_temperature", true);

        USE_ITEM_BURN_VALUE = builder.comment(
            " Should the burnable items be used if they are not defined in the datapack fuel maps",
            " The burn value for the item will be used as just he burn ticks."
        ).define("use_item_burn_value", true);

        ITEM_FUEL_HEAT = builder.comment(
            " The heat generated for any item not defined by the item fuel maps."
        ).defineInRange("item_fuel_heat", 800, 0, Integer.MAX_VALUE);

        builder.pop();

        builder.pop();

        SPEC = builder.build();
    }

    public static int getCrucibleSize() {
        return maxCrucibleSize;
    }

    public static boolean isHeatEnabled() {
        return enableHeatRequirement;
    }

    public static boolean useBiomeTemperature() {
        return useBiomeTemperature;
    }

    public static int getBaseTemperature() {
        return baseTemperature;
    }

    public static double getUltraWarmModifier() {
        return ultraWarmModifier;
    }

    public static boolean useFluidTemperature() {
        return useFluidTemperature;
    }

    public static boolean useItemBurnValue() {
        return useItemBurnValue;
    }

    public static int getItemFuelHeat() {
        return itemFuelHeat;
    }

    private static void reloadConfigs() {
        maxCrucibleSize = MAX_CRUCIBLE_SIZE.get();

        enableHeatRequirement = ENABLE_HEAT_REQUIREMENT.get();
        useBiomeTemperature = USE_BIOME_TEMPERATURE.get();
        baseTemperature = BASE_TEMPERATURE.get();
        ultraWarmModifier = ULTRA_WARM_MODIFIER.get();

        useFluidTemperature = USE_FLUID_TEMPERATURE.get();
        useItemBurnValue = USE_ITEM_BURN_VALUE.get();
        itemFuelHeat = ITEM_FUEL_HEAT.get();
    }

    @SubscribeEvent
    public static void loadConfigs(ModConfigEvent.Reloading event) {
        if (event.getConfig().getModId().equals(ModRef.ID)) {
            reloadConfigs();
        }
    }

    @SubscribeEvent
    public static void loadConfigs(ModConfigEvent.Loading event) {
        if (event.getConfig().getModId().equals(ModRef.ID)) {
            reloadConfigs();
        }
    }

}

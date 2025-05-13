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
    private static final ModConfigSpec.DoubleValue SMELTING_HEAT_BONUS_MULTIPLIER;
    private static final ModConfigSpec.DoubleValue ALLOYING_HEAT_BONUS_MULTIPLIER;

    private static final ModConfigSpec.DoubleValue ORE_MULTIPLIER;
    private static final ModConfigSpec.IntValue INGOT_MB;
    private static final ModConfigSpec.IntValue BLOCK_MB;
    private static final ModConfigSpec.IntValue NUGGET_MB;

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

    // Recipes
    private static float smeltingHeatBonusMultiplier;
    private static float alloyingHeatBonusMultiplier;

    // Constants
    private static double oreMultiplier;
    private static double ingotMb;
    private static double blockMb;
    private static double nuggetMb;


    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment(
            " Crucible Configs"
        ).push("crucible"); {
            MAX_CRUCIBLE_SIZE = builder.comment(
                " Max (external) width/depth of the crucible."
            ).defineInRange("max_size", 23, 3, 100);

            builder.comment(
                " Heat Temperature Settings"
            ).push("heat"); {
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
            }

            builder.comment(
                " Fuel Settings"
            ).push("fuel"); {
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
            }
            builder.pop();
        }

        builder.comment(
            " Recipe Settings"
        ).push("recipe"); {
            builder.push("smelting"); {
                SMELTING_HEAT_BONUS_MULTIPLIER = builder.comment(
                    " The bonus smelting speed multiplier based on the heat of the crucible.",
                    " For example, when the crucible is at 2x required heat, the speed of the smelting is '2 * <heat_bonus> * (1/<base_recipe_time>)' per tick"
                ).defineInRange("heat_bonus_multiplier", 1.0, 1.0, 10.0);
                builder.pop();
            }

            builder.push("alloying"); {
                ALLOYING_HEAT_BONUS_MULTIPLIER = builder.comment(
                    " The bonus alloying speed multiplier based on the heat of the crucible.",
                    " For example, when the crucible is at 2x required heat, the speed of the alloying is '2 * <heat_bonus>' iteration per tick"
                ).defineInRange("heat_bonus_multiplier", 10.0, 1.0, 10.0);

                builder.pop();
            }

            builder.pop();
        }

        builder.comment(
            " Configurable values used in recipes and such.",
            " Modify these to automatically change the specific values in recipe without having to modify recipes themselves"
        ).push("constants"); {
            ORE_MULTIPLIER = builder.comment(
                " How much fluid to generate from smelting raw ores.",
                " This takes into account single raw item and raw blocks.",
                " Example: 1 Raw Iron => <ingot_amount> * <ore_multiplier>",
                "                                   144 * 2"
            ).defineInRange("ore_multiplier", 2.0, 0.0, 100.0);

            INGOT_MB = builder.comment(
                " Amount of mb in an ingot."
            ).defineInRange("ingot_mb", 144, 1, Integer.MAX_VALUE);

            BLOCK_MB = builder.comment(
                " Amount of mb in a block."
            ).defineInRange("block_mb", 1296, 1, Integer.MAX_VALUE);

            NUGGET_MB = builder.comment(
                " Amount of mb in a nugget."
            ).defineInRange("nugget_mb", 16, 1, Integer.MAX_VALUE);
        }

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

    public static float getSmeltingHeatBonusMultiplier() {
        return smeltingHeatBonusMultiplier;
    }

    public static float getAlloyingHeatBonusMultiplier() {
        return alloyingHeatBonusMultiplier;
    }

    public static double getOreMultiplier() {
        return oreMultiplier;
    }

    public static double getIngotMb() {
        return ingotMb;
    }

    public static double getBlockMb() {
        return blockMb;
    }

    public static double getNuggetMb() {
        return nuggetMb;
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

        smeltingHeatBonusMultiplier = SMELTING_HEAT_BONUS_MULTIPLIER.get().floatValue();
        alloyingHeatBonusMultiplier = ALLOYING_HEAT_BONUS_MULTIPLIER.get().floatValue();

        oreMultiplier = ORE_MULTIPLIER.get();
        ingotMb = INGOT_MB.get();
        blockMb = BLOCK_MB.get();
        nuggetMb = NUGGET_MB.get();
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

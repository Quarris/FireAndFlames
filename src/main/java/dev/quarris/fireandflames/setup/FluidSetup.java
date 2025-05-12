package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.util.fluid.CustomFluidHolder;
import dev.quarris.fireandflames.util.fluid.CustomFluidRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;

public class FluidSetup {

    public static final CustomFluidRegistry REGISTRY = new CustomFluidRegistry(ModRef.ID);

    public static final CustomFluidHolder MOLTEN_IRON = REGISTRY.register("molten_iron", CustomFluidHolder.builder(() ->
            new FluidType(
                FluidType.Properties.create()
                    .pathType(PathType.LAVA)
                    .adjacentPathType(null)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                    .canDrown(false)
                    .canSwim(false)
                    .lightLevel(10)
                    .density(3000)
                    .viscosity(6000)
                    .temperature(1100)
            ))
        .customBlockProperties(props -> props.mapColor(MapColor.FIRE).lightLevel(state -> 10)));

    public static final CustomFluidHolder MOLTEN_GOLD = REGISTRY.register("molten_gold", CustomFluidHolder.builder(() ->
            new FluidType(
                FluidType.Properties.create()
                    .pathType(PathType.LAVA)
                    .adjacentPathType(null)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                    .canDrown(false)
                    .canSwim(false)
                    .lightLevel(10)
                    .density(3000)
                    .viscosity(6000)
                    .temperature(1100)
            ))
        .customBlockProperties(props -> props.mapColor(MapColor.FIRE).lightLevel(state -> 10)));

    public static final CustomFluidHolder MOLTEN_COPPER = REGISTRY.register("molten_copper", CustomFluidHolder.builder(() ->
            new FluidType(
                FluidType.Properties.create()
                    .pathType(PathType.LAVA)
                    .adjacentPathType(null)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                    .canDrown(false)
                    .canSwim(false)
                    .lightLevel(10)
                    .density(3000)
                    .viscosity(6000)
                    .temperature(1100)
            ))
        .customBlockProperties(props -> props.mapColor(MapColor.FIRE).lightLevel(state -> 10)));

    public static void init(IEventBus modBus) {
        REGISTRY.register(modBus);
    }
}

package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class FluidSetup {

    public static final DeferredRegister<FluidType> TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, ModRef.ID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, ModRef.ID);

    public static final DeferredHolder<FluidType, FluidType> MOLTEN_IRON_TYPE = TYPES.register("molten_iron", () -> new FluidType(
        FluidType.Properties.create()
            .pathType(PathType.LAVA)
            .adjacentPathType(null)
            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
            .lightLevel(10)
            .density(3000)
            .viscosity(6000)
            .temperature(1100)
        )
    );

    public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> MOLTEN_IRON = FLUIDS.register("molten_iron", () -> new BaseFlowingFluid.Source(FluidSetup.IRON_FLUID_PROPERTIES));
    public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FLOWING_MOLTEN_IRON = FLUIDS.register("flowing_molten_iron", () -> new BaseFlowingFluid.Flowing(FluidSetup.IRON_FLUID_PROPERTIES));
    private static final BaseFlowingFluid.Properties IRON_FLUID_PROPERTIES = new BaseFlowingFluid.Properties(FluidSetup.MOLTEN_IRON_TYPE::value, FluidSetup.MOLTEN_IRON::value, FluidSetup.FLOWING_MOLTEN_IRON::value)
        .bucket(ItemSetup.MOLTEN_IRON_BUCKET::asItem)
        .block(BlockSetup.MOLTEN_IRON);


    public static void init(IEventBus modBus) {
        TYPES.register(modBus);
        FLUIDS.register(modBus);
    }


}

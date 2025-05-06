package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.world.fluid.component.FluidContainerContents;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DataComponentSetup {

    public static final DeferredRegister.DataComponents REGISTRY = DeferredRegister.createDataComponents(ModRef.ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<FluidContainerContents>> FLUID_CONTAINER = REGISTRY.registerComponentType(
        "fluid_container", builder -> builder.persistent(FluidContainerContents.CODEC).networkSynchronized(FluidContainerContents.STREAM_CODEC).cacheEncoding()
    );

    public static void init(IEventBus modBus) {
        REGISTRY.register(modBus);
    }

}

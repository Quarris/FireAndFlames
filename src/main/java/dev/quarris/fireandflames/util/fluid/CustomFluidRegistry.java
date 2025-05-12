package dev.quarris.fireandflames.util.fluid;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Collection;
import java.util.HashMap;

public class CustomFluidRegistry {

    private final String modId;
    private final HashMap<ResourceLocation, CustomFluidHolder> entries = new HashMap<>();
    private final DeferredRegister<FluidType> fluidTypeRegistry;
    private final DeferredRegister<Fluid> fluidRegistry;
    private final DeferredRegister.Items itemRegistry;
    private final DeferredRegister.Blocks blockRegistry;

    public CustomFluidRegistry(String modId) {
        this.modId = modId;
        this.fluidRegistry = DeferredRegister.create(Registries.FLUID, modId);
        this.fluidTypeRegistry = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, modId);
        this.itemRegistry = DeferredRegister.createItems(modId);
        this.blockRegistry = DeferredRegister.createBlocks(modId);
    }

    public void register(IEventBus modBus) {
        fluidTypeRegistry.register(modBus);
        fluidRegistry.register(modBus);
        itemRegistry.register(modBus);
        blockRegistry.register(modBus);
    }

    public CustomFluidHolder register(String name, CustomFluidHolder.Builder builder) {
        CustomFluidHolder fluidHolder = builder.build(
            this.fluidTypeRegistry,
            this.fluidRegistry,
            this.itemRegistry,
            this.blockRegistry,
            ResourceLocation.fromNamespaceAndPath(this.modId, name)
        );
        this.entries.put(fluidHolder.getId(), fluidHolder);
        return fluidHolder;
    }

    public Collection<CustomFluidHolder> entries() {
        return this.entries.values();
    }

    public Collection<DeferredHolder<Item, ? extends Item>> getBucketEntries() {
        return this.itemRegistry.getEntries();
    }
}

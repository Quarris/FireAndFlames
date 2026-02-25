package dev.quarris.fireandflames.event.handler;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.data.tool.material.ToolMaterial;
import dev.quarris.fireandflames.data.tool.part.PartType;
import dev.quarris.fireandflames.data.tool.part.ToolPart;
import dev.quarris.fireandflames.setup.*;
import dev.quarris.fireandflames.world.crucible.fuel.FluidHandlerFuelWrapper;
import dev.quarris.fireandflames.world.crucible.fuel.ItemHandlerFuelWrapper;
import dev.quarris.fireandflames.world.item.tool.PartItem;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Optional;

@EventBusSubscriber(modid = ModRef.ID)
public class SetupEvents {

    @SubscribeEvent
    private static void registerReloadListeners(AddReloadListenerEvent event) {
        event.addListener(ModRef.DATA_MANAGER.getToolModifiers());
    }

    @EventBusSubscriber(modid = ModRef.ID, bus = EventBusSubscriber.Bus.MOD)
    public static class Mod {

        @SubscribeEvent
        private static void registerCapabilities(RegisterCapabilitiesEvent event) {
            // Item Handler
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntitySetup.CRUCIBLE_CONTROLLER.get(), (be, dir) -> be.getInventory());
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntitySetup.CASTING_BASIN.get(), (be, dir) -> be.getInventory());
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntitySetup.CASTING_TABLE.get(), (be, dir) -> be.getInventory());
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntitySetup.CRUCIBLE_BURNER.get(), (be, dir) -> be.getInventory());

            // Fluid Handler
            event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntitySetup.CRUCIBLE_DRAIN.get(), (be, dir) -> be.getCrucibleTank().orElse(null));
            event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntitySetup.CASTING_BASIN.get(), (be, dir) -> be.getTank());
            event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntitySetup.CASTING_TABLE.get(), (be, dir) -> be.getTank());
            event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntitySetup.CRUCIBLE_TANK.get(), (be, dir) -> be.getFluidTank());

            for (Block block : BuiltInRegistries.BLOCK) {
                if (event.isBlockRegistered(Capabilities.FluidHandler.BLOCK, block)) {
                    event.registerBlock(CapabilitySetup.FUEL_PROVIDER, (level, pos, state, blockEntity, context) -> {
                        if (level instanceof ServerLevel serverLevel) {
                            return new FluidHandlerFuelWrapper(serverLevel, pos, context);
                        }

                        return null;
                    }, block);
                } else if (event.isBlockRegistered(Capabilities.ItemHandler.BLOCK, block)) {
                    event.registerBlock(CapabilitySetup.FUEL_PROVIDER, (level, pos, state, blockEntity, context) -> {
                        if (level instanceof ServerLevel serverLevel) {
                            return new ItemHandlerFuelWrapper(serverLevel, pos, context);
                        }

                        return null;
                    }, block);
                }
            }
        }

        @SubscribeEvent
        public static void registerDatapackRegister(DataPackRegistryEvent.NewRegistry event) {
            event.dataPackRegistry(RegistrySetup.Keys.MATERIALS, ToolMaterial.CODEC, ToolMaterial.CODEC, builder -> builder.sync(true));
        }

        @SubscribeEvent(priority = EventPriority.LOW)
        public static void registerPartItems(RegisterEvent event) {
            if (event.getRegistryKey() == RegistrySetup.Keys.PART_TYPES) {
                for (ResourceKey<PartType> key : RegistrySetup.PART_TYPES.registryKeySet()) {
                    Optional<Holder.Reference<PartType>> optional = RegistrySetup.PART_TYPES.getHolder(key.location());
                    optional.ifPresent(holder -> PartTypeSetup.REGISTERED_PARTS.put(holder.key().location(), Registry.register(BuiltInRegistries.ITEM, holder.key().location(), new PartItem(holder, new Item.Properties().component(DataComponentSetup.TOOL_PART, ToolPart.EMPTY)))));
                }
            }
        }
    }
}

package dev.quarris.fireandflames.event.handler;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.setup.BlockEntitySetup;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = ModRef.ID, bus = EventBusSubscriber.Bus.MOD)
public class SetupEvents {

    @SubscribeEvent
    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Item Handler
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntitySetup.CRUCIBLE_CONTROLLER.get(), (be, dir) -> be.getInventory());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BlockEntitySetup.CASTING_BASIN.get(), (be, dir) -> be.getInventory());

        // Fluid Handler
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntitySetup.CRUCIBLE_DRAIN.get(), (be, dir) -> be.getCrucibleTank().orElse(null));
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BlockEntitySetup.CASTING_BASIN.get(), (be, dir) -> be.getTank());
    }

}

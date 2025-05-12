package dev.quarris.fireandflames.event.handler;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.setup.BlockEntitySetup;
import dev.quarris.fireandflames.setup.CapabilitySetup;
import dev.quarris.fireandflames.world.crucible.fuel.FluidHandlerFuelWrapper;
import dev.quarris.fireandflames.world.crucible.fuel.IFuelProvider;
import dev.quarris.fireandflames.world.crucible.fuel.ItemHandlerFuelWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = ModRef.ID, bus = EventBusSubscriber.Bus.MOD)
public class SetupEvents {

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

}

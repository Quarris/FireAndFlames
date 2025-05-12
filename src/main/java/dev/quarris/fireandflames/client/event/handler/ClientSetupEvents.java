package dev.quarris.fireandflames.client.event.handler;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.client.renderer.blockentity.*;
import dev.quarris.fireandflames.client.screen.CrucibleBurnerScreen;
import dev.quarris.fireandflames.client.screen.CrucibleScreen;
import dev.quarris.fireandflames.setup.BlockEntitySetup;
import dev.quarris.fireandflames.setup.FluidSetup;
import dev.quarris.fireandflames.setup.MenuSetup;
import dev.quarris.fireandflames.util.fluid.CustomFluidHolder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = ModRef.ID, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetupEvents {

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        for (CustomFluidHolder fluidHolder : FluidSetup.REGISTRY.entries()) {
            event.registerFluidType(fluidHolder.getFluidExtensions(), fluidHolder.getFluidType());
        }
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(MenuSetup.CRUCIBLE.get(), CrucibleScreen::new);
        event.register(MenuSetup.CRUCIBLE_BURNER.get(), CrucibleBurnerScreen::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntitySetup.CRUCIBLE_CONTROLLER.get(), CrucibleControllerRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitySetup.CRUCIBLE_FAWSIT.get(), CrucibleFawsitRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitySetup.CASTING_BASIN.get(), CastingBasinRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitySetup.CASTING_TABLE.get(), CastingTableRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitySetup.CRUCIBLE_TANK.get(), FluidStorageRenderer::new);
    }

}

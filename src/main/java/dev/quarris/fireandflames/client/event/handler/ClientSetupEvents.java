package dev.quarris.fireandflames.client.event.handler;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.client.ModClient;
import dev.quarris.fireandflames.client.model.CustomToolModelLoader;
import dev.quarris.fireandflames.client.renderer.blockentity.*;
import dev.quarris.fireandflames.client.screen.*;
import dev.quarris.fireandflames.client.util.extensions.CustomToolClientExtensions;
import dev.quarris.fireandflames.setup.*;
import dev.quarris.fireandflames.util.fluid.CustomFluidHolder;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = ModRef.ID, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetupEvents {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ModClient.get().init(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        for (CustomFluidHolder fluidHolder : FluidSetup.REGISTRY.entries()) {
            event.registerFluidType(fluidHolder.getFluidExtensions(), fluidHolder.getFluidType());
        }

        event.registerItem(new CustomToolClientExtensions(), ToolItemSetup.PICKAXE);
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(MenuSetup.CRUCIBLE.get(), CrucibleScreen::new);
        event.register(MenuSetup.CRUCIBLE_BURNER.get(), CrucibleBurnerScreen::new);
        event.register(MenuSetup.TINKERS_WORKBENCH.get(), TinkersWorkbenchScreen::new);
        event.register(MenuSetup.SMITHING_ANVIL.get(), SmithingAnvilScreen::new);
        event.register(MenuSetup.ARTISAN_TABLE.get(), ArtisanTableScreen::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntitySetup.CRUCIBLE_CONTROLLER.get(), CrucibleControllerRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitySetup.CRUCIBLE_FAWSIT.get(), CrucibleFawsitRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitySetup.CASTING_BASIN.get(), CastingBasinRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitySetup.CASTING_TABLE.get(), CastingTableRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitySetup.CRUCIBLE_TANK.get(), FluidStorageRenderer::new);
    }

    @SubscribeEvent
    public static void registerCustomModelLoader(ModelEvent.RegisterGeometryLoaders event) {
        event.register(CustomToolModelLoader.ID, CustomToolModelLoader.INSTANCE);
    }

}

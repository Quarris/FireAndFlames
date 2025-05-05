package dev.quarris.fireandflames.client.event.handler;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.client.renderer.blockentity.CastingBasinRenderer;
import dev.quarris.fireandflames.client.renderer.blockentity.CastingTableRenderer;
import dev.quarris.fireandflames.client.renderer.blockentity.CrucibleControllerRenderer;
import dev.quarris.fireandflames.client.renderer.blockentity.CrucibleFawsitRenderer;
import dev.quarris.fireandflames.client.screen.CrucibleScreen;
import dev.quarris.fireandflames.setup.BlockEntitySetup;
import dev.quarris.fireandflames.setup.FluidSetup;
import dev.quarris.fireandflames.setup.MenuSetup;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForgeMod;

@EventBusSubscriber(modid = ModRef.ID, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetupEvents {

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            private static final ResourceLocation IRON_STILL = ModRef.res("block/molten_iron");
            private static final ResourceLocation IRON_FLOW = ModRef.res("block/flowing_molten_iron");

            @Override
            public ResourceLocation getStillTexture() {
                return IRON_STILL;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return IRON_FLOW;
            }
        }, FluidSetup.MOLTEN_IRON_TYPE);
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(MenuSetup.CRUCIBLE.get(), CrucibleScreen::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntitySetup.CRUCIBLE_CONTROLLER.get(), CrucibleControllerRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitySetup.CRUCIBLE_FAWSIT.get(), CrucibleFawsitRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitySetup.CASTING_BASIN.get(), CastingBasinRenderer::new);
        event.registerBlockEntityRenderer(BlockEntitySetup.CASTING_TABLE.get(), CastingTableRenderer::new);
    }

}

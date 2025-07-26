package dev.quarris.fireandflames.client.event.handler;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.client.model.ToolModelLoader;
import dev.quarris.fireandflames.client.renderer.blockentity.*;
import dev.quarris.fireandflames.client.renderer.tool.ToolRenderer;
import dev.quarris.fireandflames.client.screen.*;
import dev.quarris.fireandflames.data.tool.ICustomTool;
import dev.quarris.fireandflames.data.tool.ToolData;
import dev.quarris.fireandflames.data.tool.ToolType;
import dev.quarris.fireandflames.data.tool.material.IMaterialHolder;
import dev.quarris.fireandflames.data.tool.material.ToolMaterial;
import dev.quarris.fireandflames.data.tool.part.ICustomPart;
import dev.quarris.fireandflames.data.tool.part.PartSlot;
import dev.quarris.fireandflames.data.tool.part.ToolParts;
import dev.quarris.fireandflames.setup.*;
import dev.quarris.fireandflames.util.fluid.CustomFluidHolder;
import dev.quarris.fireandflames.world.item.tool.PartItem;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = ModRef.ID, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetupEvents {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ToolRenderer.init();
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        for (CustomFluidHolder fluidHolder : FluidSetup.REGISTRY.entries()) {
            event.registerFluidType(fluidHolder.getFluidExtensions(), fluidHolder.getFluidType());
        }

        /*event.registerItem(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return ToolRenderer.get().getRenderer();
            }
        }, ToolItemSetup.PICKAXE, ItemSetup.TEST);*/
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
        event.register(ToolModelLoader.ID, ToolModelLoader.INSTANCE);
    }

    @SubscribeEvent
    private static void registerItemColor(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tint) -> {
            ToolData toolData = stack.get(DataComponentSetup.TOOL_DATA);
            if (toolData == null || !(stack.getItem() instanceof ICustomTool tool)) {
                return -1;
            }

            ToolType<? extends ICustomTool> toolType = tool.getType();
            ToolParts toolParts = toolData.toolParts();

            for (PartSlot part : toolType.parts().values()) {
                if (tint == part.index()) {
                    Holder<ToolMaterial> partMaterial = toolParts.getPart(part.name()).material();
                    return partMaterial.value().color().packed();
                }
            }

            return -1;
        }, ToolItemSetup.PICKAXE, ToolItemSetup.AXE, ToolItemSetup.SHOVEL, ToolItemSetup.HOE, ToolItemSetup.SWORD, ToolItemSetup.HAMMER);

        List<ItemLike> shapes = new ArrayList<>(PartTypeSetup.REGISTERED_PARTS.values());
        shapes.addAll(List.of(ToolItemSetup.BLADE_SHAPE, ToolItemSetup.TOOL_SHAPE, ToolItemSetup.MISC_SHAPE));
        event.register((stack, tint) -> {
            if (!(stack.getItem() instanceof IMaterialHolder holder) || !holder.hasMaterial(stack)) {
                return -1;
            }

            Holder<ToolMaterial> partMaterial = holder.getMaterial(stack);
            return partMaterial.value().color().packed();
        }, shapes.toArray(new ItemLike[0]));
    }
}

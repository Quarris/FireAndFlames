package dev.quarris.fireandflames.datagen.client;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.client.model.ToolModelLoader;
import dev.quarris.fireandflames.client.renderer.tool.ToolItemRenderer;
import dev.quarris.fireandflames.datagen.client.model.ToolModelBuilder;
import dev.quarris.fireandflames.setup.ItemSetup;
import dev.quarris.fireandflames.setup.PartTypeSetup;
import dev.quarris.fireandflames.setup.ToolItemSetup;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.loaders.ItemLayerModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.util.TransformationHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

public class ItemModelGen extends ItemModelProvider {

    public ItemModelGen(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ModRef.ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        this.basicItem(ItemSetup.FIRE_CLAY_BALL.get());
        this.basicItem(ItemSetup.FIRE_BRICK.get());
        this.basicItem(ItemSetup.INGOT_CAST.get());
        this.basicItem(ItemSetup.NUGGET_CAST.get());
        this.basicItem(ItemSetup.TOOL_SHAPE_CAST.get());
        this.basicItem(ItemSetup.MISC_SHAPE_CAST.get());
        this.basicItem(ItemSetup.BLADE_SHAPE_CAST.get());

        this.tool(ToolItemSetup.PICKAXE)
            .part(this.modLoc("item/tool/pickaxe/handle"), 0)
            .part(this.modLoc("item/tool/pickaxe/pickaxe_head"), 1)
            .part(this.modLoc("item/tool/pickaxe/grip"), 2);

        this.tool(ToolItemSetup.AXE)
            .part(this.modLoc("item/tool/axe/handle"), 0)
            .part(this.modLoc("item/tool/axe/axe_head"), 1)
            .part(this.modLoc("item/tool/axe/binding"), 2);

        this.tool(ToolItemSetup.SHOVEL)
            .part(this.modLoc("item/tool/shovel/handle"), 0)
            .part(this.modLoc("item/tool/shovel/shovel_head"), 1)
            .part(this.modLoc("item/tool/shovel/binding"), 2);

        this.tool(ToolItemSetup.HOE)
            .part(this.modLoc("item/tool/hoe/handle"), 0)
            .part(this.modLoc("item/tool/hoe/hoe_head"), 1)
            .part(this.modLoc("item/tool/hoe/grip"), 2);

        this.tool(ToolItemSetup.SWORD)
            .part(this.modLoc("item/tool/sword/handle"), 0)
            .part(this.modLoc("item/tool/sword/wide_guard"), 1)
            .part(this.modLoc("item/tool/sword/sword_blade"), 2);

        this.tool(ToolItemSetup.HAMMER)
            .part(this.modLoc("item/tool/hammer/handle"), 0)
            .part(this.modLoc("item/tool/hammer/binding"), 1)
            .part(this.modLoc("item/tool/hammer/grip"), 2)
            .part(this.modLoc("item/tool/hammer/hammer_left"), 3)
            .part(this.modLoc("item/tool/hammer/hammer_right"), 4);

        this.basicItem(PartTypeSetup.AXE_HEAD.getKey().location()).customLoader(ItemLayerModelBuilder::begin);
        this.basicItem(PartTypeSetup.PICKAXE_HEAD.getKey().location()).customLoader(ItemLayerModelBuilder::begin);
        this.basicItem(PartTypeSetup.SHOVEL_HEAD.getKey().location()).customLoader(ItemLayerModelBuilder::begin);
        this.basicItem(PartTypeSetup.HOE_HEAD.getKey().location()).customLoader(ItemLayerModelBuilder::begin);
        this.basicItem(PartTypeSetup.SWORD_BLADE.getKey().location()).customLoader(ItemLayerModelBuilder::begin);
        this.basicItem(PartTypeSetup.HANDLE.getKey().location()).customLoader(ItemLayerModelBuilder::begin);
        this.basicItem(PartTypeSetup.BINDING.getKey().location()).customLoader(ItemLayerModelBuilder::begin);
        this.basicItem(PartTypeSetup.WIDE_GUARD.getKey().location()).customLoader(ItemLayerModelBuilder::begin);

        this.basicItem(ToolItemSetup.TOOL_SHAPE.get()).customLoader(ItemLayerModelBuilder::begin);
        this.basicItem(ToolItemSetup.BLADE_SHAPE.get()).customLoader(ItemLayerModelBuilder::begin);
        this.basicItem(ToolItemSetup.MISC_SHAPE.get()).customLoader(ItemLayerModelBuilder::begin);
    }

    private ToolModelBuilder<ItemModelBuilder> tool(DeferredItem<?> item) {
        return this.getBuilder(item.getRegisteredName())
            .parent(this.getExistingFile(ResourceLocation.withDefaultNamespace("item/handheld")))
            .customLoader(ToolModelBuilder::begin);
    }
}

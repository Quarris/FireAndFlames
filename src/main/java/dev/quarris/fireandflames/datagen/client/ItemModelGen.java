package dev.quarris.fireandflames.datagen.client;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.setup.ItemSetup;
import dev.quarris.fireandflames.setup.PartTypeSetup;
import dev.quarris.fireandflames.setup.ToolItemSetup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

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

        this.basicItem(ToolItemSetup.PICKAXE.get());
        this.basicItem(ToolItemSetup.AXE.get());
        this.basicItem(ToolItemSetup.SHOVEL.get());
        this.basicItem(ToolItemSetup.HOE.get());
        this.basicItem(ToolItemSetup.HAMMER.get());
        this.basicItem(ToolItemSetup.SWORD.get());

        this.basicItem(PartTypeSetup.AXE_HEAD.getKey().location());
        this.basicItem(PartTypeSetup.PICKAXE_HEAD.getKey().location());
        this.basicItem(PartTypeSetup.SHOVEL_HEAD.getKey().location());
        this.basicItem(PartTypeSetup.HOE_HEAD.getKey().location());
        this.basicItem(PartTypeSetup.SWORD_BLADE.getKey().location());
        this.basicItem(PartTypeSetup.HANDLE.getKey().location());
        this.basicItem(PartTypeSetup.BINDING.getKey().location());
        this.basicItem(PartTypeSetup.WIDE_GUARD.getKey().location());

        this.basicItem(ToolItemSetup.TOOL_SHAPE.get());
        this.basicItem(ToolItemSetup.BLADE_SHAPE.get());
        this.basicItem(ToolItemSetup.MISC_SHAPE.get());

        //this.getBuilder("custom_tool").customLoader(CustomToolLoaderBuilder::new);
    }
}

package dev.quarris.fireandflames.datagen.client;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.setup.ItemSetup;
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

        this.basicItem(ToolItemSetup.PICKAXE.get());
        this.basicItem(ToolItemSetup.AXE.get());
        this.basicItem(ToolItemSetup.SHOVEL.get());
        this.basicItem(ToolItemSetup.HOE.get());
        this.basicItem(ToolItemSetup.HAMMER.get());
        this.basicItem(ToolItemSetup.SWORD.get());

        this.basicItem(ToolItemSetup.AXE_HEAD.get());
        this.basicItem(ToolItemSetup.PICKAXE_HEAD.get());
        this.basicItem(ToolItemSetup.SHOVEL_HEAD.get());
        this.basicItem(ToolItemSetup.HOE_HEAD.get());
        this.basicItem(ToolItemSetup.SWORD_BLADE.get());

        this.basicItem(ToolItemSetup.HANDLE.get());
        this.basicItem(ToolItemSetup.BINDING.get());
        this.basicItem(ToolItemSetup.WIDE_GUARD.get());

        //this.getBuilder("custom_tool").customLoader(CustomToolLoaderBuilder::new);
    }
}

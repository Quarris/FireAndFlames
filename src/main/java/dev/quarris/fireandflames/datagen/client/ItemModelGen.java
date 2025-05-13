package dev.quarris.fireandflames.datagen.client;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.setup.ItemSetup;
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
    }
}

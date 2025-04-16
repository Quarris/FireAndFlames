package dev.quarris.fireandflames.datagen.client;

import dev.quarris.fireandflames.setup.BlockSetup;
import dev.quarris.fireandflames.setup.ItemSetup;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;

public class ModelGen extends FabricModelProvider {

    public ModelGen(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators modelGen) {
        modelGen.createTrivialCube(BlockSetup.FIRE_CLAY.get());
        modelGen.createTrivialCube(BlockSetup.FIRE_BRICKS.get());
    }

    @Override
    public void generateItemModels(ItemModelGenerators modelGen) {
        modelGen.generateFlatItem(ItemSetup.FIRE_CLAY_BALL.get(), ModelTemplates.FLAT_ITEM);
        modelGen.generateFlatItem(ItemSetup.FIRE_BRICK.get(), ModelTemplates.FLAT_ITEM);
    }
}

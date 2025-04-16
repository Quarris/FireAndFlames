package dev.quarris.fireandflames.datagen;

import dev.quarris.fireandflames.datagen.client.LanguageGen;
import dev.quarris.fireandflames.datagen.client.ModelGen;
import dev.quarris.fireandflames.datagen.server.RecipesGen;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

/**
 * Entry point for Fabric data generation.
 */
public class FabricDataGen implements DataGeneratorEntrypoint {
    
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(LanguageGen::new);
        pack.addProvider(RecipesGen::new);
        pack.addProvider(ModelGen::new);
    }

}

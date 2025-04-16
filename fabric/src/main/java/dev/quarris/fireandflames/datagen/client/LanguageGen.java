package dev.quarris.fireandflames.datagen.client;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.setup.BlockSetup;
import dev.quarris.fireandflames.setup.ItemSetup;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class LanguageGen extends FabricLanguageProvider {

    public LanguageGen(FabricDataOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookup) {
        super(pOutput, "en_us", pLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(ItemSetup.FIRE_CLAY_BALL.get(), "Fire Clay Ball");
        translationBuilder.add(ItemSetup.FIRE_BRICK.get(), "Fire Brick");

        translationBuilder.add(BlockSetup.FIRE_CLAY.get(), "Fire Clay");
        translationBuilder.add(BlockSetup.FIRE_BRICKS.get(), "Fire Bricks");
    }
}

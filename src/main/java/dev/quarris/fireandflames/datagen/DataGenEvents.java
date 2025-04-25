package dev.quarris.fireandflames.datagen;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.datagen.client.BlockStateGen;
import dev.quarris.fireandflames.datagen.client.EnUsLanguageGen;
import dev.quarris.fireandflames.datagen.client.ItemModelGen;
import dev.quarris.fireandflames.datagen.server.BlockTagGen;
import dev.quarris.fireandflames.datagen.server.ItemTagGen;
import dev.quarris.fireandflames.datagen.server.RecipesGen;
import net.minecraft.data.DataProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = ModRef.ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenEvents {

    @SubscribeEvent
    private static void gatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var existingFiles = event.getExistingFileHelper();
        var lookup = event.getLookupProvider();

        // Client
        gen.addProvider(event.includeClient(), (DataProvider.Factory<EnUsLanguageGen>) EnUsLanguageGen::new);
        gen.addProvider(event.includeClient(), (DataProvider.Factory<BlockStateGen>)(packOutput -> new BlockStateGen(packOutput, existingFiles)));
        gen.addProvider(event.includeClient(), (DataProvider.Factory<ItemModelGen>)(packOutput -> new ItemModelGen(packOutput, existingFiles)));

        // Server
        gen.addProvider(event.includeClient(), (DataProvider.Factory<RecipesGen>)(packOutput -> new RecipesGen(packOutput, lookup)));
        var blockTags = gen.addProvider(event.includeClient(), (DataProvider.Factory<BlockTagGen>)(packOutput -> new BlockTagGen(packOutput, lookup, existingFiles)));
        gen.addProvider(event.includeClient(), (DataProvider.Factory<ItemTagGen>)(packOutput -> new ItemTagGen(packOutput, lookup, blockTags.contentsGetter(), existingFiles)));
    }

}

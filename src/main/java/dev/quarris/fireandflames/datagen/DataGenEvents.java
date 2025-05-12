package dev.quarris.fireandflames.datagen;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.datagen.client.BlockStateGen;
import dev.quarris.fireandflames.datagen.client.EnUsLanguageGen;
import dev.quarris.fireandflames.datagen.client.ItemModelGen;
import dev.quarris.fireandflames.datagen.server.*;
import dev.quarris.fireandflames.datagen.server.loot.BlockLoot;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = ModRef.ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenEvents {

    @SubscribeEvent
    private static void gatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var existingFiles = event.getExistingFileHelper();
        var lookup = event.getLookupProvider();

        // Client
        gen.addProvider(event.includeClient(), (DataProvider.Factory<EnUsLanguageGen>) EnUsLanguageGen::new);
        gen.addProvider(event.includeClient(), (DataProvider.Factory<BlockStateGen>) (packOutput -> new BlockStateGen(packOutput, existingFiles)));
        gen.addProvider(event.includeClient(), (DataProvider.Factory<ItemModelGen>) (packOutput -> new ItemModelGen(packOutput, existingFiles)));

        // Server
        gen.addProvider(event.includeServer(), (DataProvider.Factory<DatapackBuiltinEntriesProvider>) output ->
            new DatapackBuiltinEntriesProvider(output, lookup, new RegistrySetBuilder()
                .add(Registries.DAMAGE_TYPE, DamageTypeGen::bootstrap),
                Set.of(ModRef.ID))
        );

        gen.addProvider(event.includeServer(), (DataProvider.Factory<RecipesGen>) (packOutput -> new RecipesGen(packOutput, lookup)));
        var blockTags = gen.addProvider(event.includeClient(), (DataProvider.Factory<BlockTagGen>) (packOutput -> new BlockTagGen(packOutput, lookup, existingFiles)));
        gen.addProvider(event.includeServer(), (DataProvider.Factory<ItemTagGen>) (packOutput -> new ItemTagGen(packOutput, lookup, blockTags.contentsGetter(), existingFiles)));
        gen.addProvider(event.includeServer(), (DataProvider.Factory<FluidTagGen>) (packOutput -> new FluidTagGen(packOutput, lookup, existingFiles)));
        gen.addProvider(event.includeServer(), (DataProvider.Factory<DamageTypeTagGen>) (packOutput -> new DamageTypeTagGen(packOutput, lookup, existingFiles)));
        gen.addProvider(event.includeServer(), (DataProvider.Factory<LootTableProvider>) output -> new LootTableProvider(output, Set.of(), List.of(
            new LootTableProvider.SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK)
        ), lookup));

        gen.addProvider(event.includeServer(), (DataProvider.Factory<DataMapGen>) (packOutput -> new DataMapGen(packOutput, lookup)));
    }

}

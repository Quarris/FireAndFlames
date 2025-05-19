package dev.quarris.fireandflames.datagen.server;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.setup.ItemSetup;
import dev.quarris.fireandflames.setup.TagSetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ItemTagGen extends ItemTagsProvider {

    public ItemTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, ModRef.ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pLookup) {
        this.tag(TagSetup.ItemTags.CASTS).add(ItemSetup.NUGGET_CAST.asItem(), ItemSetup.INGOT_CAST.asItem());
    }

    private static ResourceKey<Item> key(Block block) {
        return ResourceKey.create(Registries.ITEM, BuiltInRegistries.ITEM.getKey(block.asItem()));
    }
}

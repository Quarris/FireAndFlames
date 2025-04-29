package dev.quarris.fireandflames.datagen.server;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.setup.BlockSetup;
import dev.quarris.fireandflames.setup.TagSetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class BlockTagGen extends BlockTagsProvider {

    public BlockTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ModRef.ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pLookup) {
        HolderLookup.RegistryLookup<Block> blocks = pLookup.lookupOrThrow(Registries.BLOCK);
        this.tag(TagSetup.BlockTags.VALID_CRUCIBLE_BLOCKS).add(
            key(BlockSetup.FIRE_BRICKS.get()),
            key(BlockSetup.CRUCIBLE_CONTROLLER.get())
        );
    }

    private static ResourceKey<Block> key(Block block) {
        return ResourceKey.create(Registries.BLOCK, BuiltInRegistries.BLOCK.getKey(block));
    }
}

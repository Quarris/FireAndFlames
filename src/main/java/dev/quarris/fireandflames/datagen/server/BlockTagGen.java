package dev.quarris.fireandflames.datagen.server;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.setup.BlockSetup;
import dev.quarris.fireandflames.setup.TagSetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
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
        this.tag(TagSetup.BlockTags.VALID_CRUCIBLE_BLOCKS).add(
            BlockSetup.FIRE_BRICKS.get(),
            BlockSetup.CRUCIBLE_WINDOW.get(),
            BlockSetup.CRUCIBLE_TANK.get(),
            BlockSetup.CRUCIBLE_BURNER.get()
        );

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(
                BlockSetup.FIRE_BRICKS.get(),
                BlockSetup.CRUCIBLE_CONTROLLER.get(),
                BlockSetup.CRUCIBLE_WINDOW.get(),
                BlockSetup.CRUCIBLE_DRAIN.get(),
                BlockSetup.CRUCIBLE_TANK.get(),
                BlockSetup.CRUCIBLE_FAWSIT.get(),
                BlockSetup.CASTING_BASIN.get(),
                BlockSetup.CASTING_TABLE.get()
            );

        this.tag(BlockTags.MINEABLE_WITH_SHOVEL)
            .add(
                BlockSetup.FIRE_CLAY.get()
            );
    }

    private static ResourceKey<Block> key(Block block) {
        return ResourceKey.create(Registries.BLOCK, BuiltInRegistries.BLOCK.getKey(block));
    }
}

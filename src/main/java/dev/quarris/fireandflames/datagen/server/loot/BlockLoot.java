package dev.quarris.fireandflames.datagen.server.loot;

import dev.quarris.fireandflames.setup.BlockSetup;
import dev.quarris.fireandflames.setup.ItemSetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.Set;

public class BlockLoot extends BlockLootSubProvider {
    public BlockLoot(HolderLookup.Provider pLookup) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, pLookup);
    }

    @Override
    protected void generate() {
        this.dropSelf(BlockSetup.FIRE_BRICKS.get());
        this.dropSelf(BlockSetup.CRUCIBLE_CONTROLLER.get());
        this.dropSelf(BlockSetup.CRUCIBLE_DRAIN.get());
        this.dropSelf(BlockSetup.CRUCIBLE_WINDOW.get());
        this.dropSelf(BlockSetup.CRUCIBLE_FAWSIT.get());
        this.dropSelf(BlockSetup.CASTING_BASIN.get());

        this.add(BlockSetup.FIRE_CLAY.get(), block -> this.createSingleItemTableWithSilkTouch(block, ItemSetup.FIRE_CLAY_BALL, ConstantValue.exactly(4.0F)));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BlockSetup.REGISTRY.getEntries()
            .stream()
            .map(e -> (Block) e.value())
            .toList();
    }
}

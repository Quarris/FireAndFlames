package dev.quarris.fireandflames.datagen.server.loot;

import dev.quarris.fireandflames.setup.BlockSetup;
import dev.quarris.fireandflames.setup.DataComponentSetup;
import dev.quarris.fireandflames.setup.ItemSetup;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
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
        this.dropSelf(BlockSetup.CASTING_TABLE.get());
        this.dropSelf(BlockSetup.CRUCIBLE_BURNER.get());
        this.add(BlockSetup.CRUCIBLE_TANK.get(), this::createFluidStorageDrop);

        this.add(BlockSetup.FIRE_CLAY.get(), block -> this.createSingleItemTableWithSilkTouch(block, ItemSetup.FIRE_CLAY_BALL, ConstantValue.exactly(4.0F)));
    }

    protected LootTable.Builder createFluidStorageDrop(Block block) {
        return LootTable.lootTable()
            .withPool(
                this.applyExplosionCondition(
                    block,
                    LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .add(
                            LootItem.lootTableItem(block)
                                .apply(
                                    CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                        .include(DataComponentSetup.FLUID_CONTAINER.get())
                                )
                        )
                )
            );
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BlockSetup.REGISTRY.getEntries()
            .stream()
            .map(e -> (Block) e.value())
            .toList();
    }
}

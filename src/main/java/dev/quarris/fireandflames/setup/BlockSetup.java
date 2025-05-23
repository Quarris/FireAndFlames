package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.world.block.*;
import dev.quarris.fireandflames.world.fluid.component.FluidContainerContents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockSetup {

    public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.Blocks.createBlocks(ModRef.ID);

    public static final DeferredBlock<Block> FIRE_CLAY = registerBlock("fire_clay", Block::new,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_ORANGE)
            .strength(0.6F)
            .sound(SoundType.GRAVEL));

    public static final DeferredBlock<Block> FIRE_BRICKS = registerBlock("fire_bricks", Block::new,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_ORANGE)
            .requiresCorrectToolForDrops()
            .strength(1.0F, 2.0F)
            .sound(SoundType.STONE));

    public static final DeferredBlock<CrucibleControllerBlock> CRUCIBLE_CONTROLLER = registerBlock("crucible_controller", CrucibleControllerBlock::new,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_ORANGE)
            .requiresCorrectToolForDrops()
            .strength(1.0F, 2.0F)
            .lightLevel(state -> state.getValue(CrucibleControllerBlock.LIT) ? 15 : 0)
            .sound(SoundType.STONE));

    public static final DeferredBlock<CrucibleWindowBlock> CRUCIBLE_WINDOW = registerBlock("crucible_window", CrucibleWindowBlock::new,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_ORANGE)
            .requiresCorrectToolForDrops()
            .strength(1.0F)
            .sound(SoundType.GLASS)
            .noOcclusion()
            .isViewBlocking(((state, level, pos) -> false)));

    public static final DeferredBlock<CrucibleDrainBlock> CRUCIBLE_DRAIN = registerBlock("crucible_drain", CrucibleDrainBlock::new,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_ORANGE)
            .requiresCorrectToolForDrops()
            .strength(1.0F, 2.0F)
            .sound(SoundType.STONE));

    public static final DeferredBlock<CrucibleTankBlock> CRUCIBLE_TANK = registerBlock("crucible_tank", CrucibleTankBlock::new,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_ORANGE)
            .requiresCorrectToolForDrops()
            .strength(1.0F, 2.0F)
            .noOcclusion()
            .sound(SoundType.STONE)
            .isViewBlocking(((state, level, pos) -> false)),
        () -> new Item.Properties().component(DataComponentSetup.FLUID_CONTAINER, FluidContainerContents.EMPTY));

    public static final DeferredBlock<CrucibleBurnerBlock> CRUCIBLE_BURNER = registerBlock("crucible_burner", CrucibleBurnerBlock::new,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_ORANGE)
            .requiresCorrectToolForDrops()
            .strength(1.0F, 2.0F)
            .sound(SoundType.STONE),
        new Item.Properties());

    public static final DeferredBlock<CrucibleFawsitBlock> CRUCIBLE_FAWSIT = registerBlock("crucible_faucet", CrucibleFawsitBlock::new,
        BlockBehaviour.Properties.of()
            .forceSolidOn()
            .mapColor(MapColor.COLOR_ORANGE)
            .requiresCorrectToolForDrops()
            .strength(1.0F, 2.0F)
            .sound(SoundType.STONE));

    public static final DeferredBlock<CastingBlock> CASTING_BASIN = registerBlock("casting_basin", CastingBasinBlock::new,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_ORANGE)
            .requiresCorrectToolForDrops()
            .strength(1.0F, 2.0F)
            .sound(SoundType.STONE));

    public static final DeferredBlock<CastingBlock> CASTING_TABLE = registerBlock("casting_table", CastingTableBlock::new,
        BlockBehaviour.Properties.of()
            .mapColor(MapColor.COLOR_ORANGE)
            .requiresCorrectToolForDrops()
            .strength(1.0F, 2.0F)
            .sound(SoundType.STONE));

    // Helper methods
    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> blockSupplier, BlockBehaviour.Properties blockProps) {
        return registerBlock(name, blockSupplier, blockProps, new Item.Properties());
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> blockSupplier, BlockBehaviour.Properties blockProps, Item.Properties itemProps) {
        DeferredBlock<T> block = REGISTRY.registerBlock(name, blockSupplier, blockProps);
        ItemSetup.registerItem(name, props -> new BlockItem(block.get(), props), itemProps);
        return block;
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> blockSupplier, BlockBehaviour.Properties blockProps, Supplier<Item.Properties> itemProps) {
        DeferredBlock<T> block = REGISTRY.registerBlock(name, blockSupplier, blockProps);
        ItemSetup.registerItem(name, () -> new BlockItem(block.get(), itemProps.get()));
        return block;
    }

    private static <T extends Block> DeferredBlock<T> registerBlockNoItem(String name, Function<BlockBehaviour.Properties, T> blockSupplier, BlockBehaviour.Properties blockProps) {
        return REGISTRY.registerBlock(name, blockSupplier, blockProps);
    }

    public static void init(IEventBus modBus) {
        REGISTRY.register(modBus);
    }
}

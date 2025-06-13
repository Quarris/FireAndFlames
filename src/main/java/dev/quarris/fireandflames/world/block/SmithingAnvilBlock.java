package dev.quarris.fireandflames.world.block;

import com.mojang.serialization.MapCodec;
import dev.quarris.fireandflames.world.inventory.menu.SmithingAnvilMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SmithingAnvilBlock extends HorizontalDirectionalBlock {

    public static final Map<Direction, VoxelShape> SHAPES = new HashMap<>() {{
        put(Direction.NORTH, Block.box(0, 0, 4, 16, 12, 12));
        put(Direction.EAST, Block.box(4, 0, 0, 12, 12, 16));
        put(Direction.SOUTH, Block.box(0, 0, 4, 16, 12, 12));
        put(Direction.WEST, Block.box(4, 0, 0, 12, 12, 16));
    }};

    public SmithingAnvilBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        player.openMenu(new SimpleMenuProvider((id, inv, pl) -> new SmithingAnvilMenu(id, inv), Component.translatable("container.fireandflames.smithing_anvil.title")));
        return InteractionResult.CONSUME;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return simpleCodec(SmithingAnvilBlock::new);
    }
}

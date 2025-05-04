package dev.quarris.fireandflames.world.block;

import com.mojang.serialization.MapCodec;
import dev.quarris.fireandflames.setup.BlockEntitySetup;
import dev.quarris.fireandflames.world.block.entity.CrucibleFawsitBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CrucibleFawsitBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public static final Map<Direction, VoxelShape> SHAPES = new HashMap<>() {{
        put(Direction.NORTH, Shapes.or(Block.box(3, 4, 10, 13, 8, 16), Block.box(3, 8, 14, 13, 12, 16)));
        put(Direction.SOUTH, Shapes.or(Block.box(3, 4, 0, 13, 8, 6), Block.box(3, 8, 0, 13, 12, 2)));
        put(Direction.EAST, Shapes.or(Block.box(0, 4, 3, 6, 8, 13), Block.box(0, 8, 3, 2, 12, 13)));
        put(Direction.WEST, Shapes.or(Block.box(10, 4, 3, 16, 8, 13), Block.box(14, 8, 3, 16, 12, 13)));
    }};

    public CrucibleFawsitBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pRayTrace) {
        if (pLevel.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        boolean consume = pLevel.getBlockEntity(pPos, BlockEntitySetup.CRUCIBLE_FAWSIT.get()).map(fawsit -> {
            fawsit.toggle();
            return true;
        }).orElse(false);

        return consume ? InteractionResult.CONSUME : super.useWithoutItem(pState, pLevel, pPos, pPlayer, pRayTrace);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPES.get(pState.getValue(FACING));
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrucibleFawsitBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null :
            createTickerHelper(blockEntityType, BlockEntitySetup.CRUCIBLE_FAWSIT.get(), CrucibleFawsitBlockEntity::serverTick);
    }

    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> typeCheck, BlockEntityType<E> desiredType, BlockEntityTicker<? super E> ticker) {
        return desiredType == typeCheck ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(CrucibleFawsitBlock::new);
    }
}

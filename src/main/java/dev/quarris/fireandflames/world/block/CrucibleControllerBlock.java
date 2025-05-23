package dev.quarris.fireandflames.world.block;

import com.mojang.serialization.MapCodec;
import dev.quarris.fireandflames.world.block.entity.CrucibleControllerBlockEntity;
import dev.quarris.fireandflames.setup.BlockEntitySetup;
import dev.quarris.fireandflames.world.crucible.CrucibleStructure;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.List;

public class CrucibleControllerBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public CrucibleControllerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(LIT, false));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltips, TooltipFlag tooltipFlag) {
        tooltips.add(Component.translatable("block.fireandflames.crucible_controller.description").withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(CrucibleControllerBlock::new);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        // Face the player when placed
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pRayTrace) {
        if (FluidUtil.interactWithFluidHandler(pPlayer, pHand, pLevel, pPos, pRayTrace.getDirection())) {
            return ItemInteractionResult.sidedSuccess(pLevel.isClientSide());
        }

        return super.useItemOn(pStack, pState, pLevel, pPos, pPlayer, pHand, pRayTrace);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pRayTrace) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof CrucibleControllerBlockEntity crucible) {
            if (crucible.getStructure() == null) {
                pPlayer.displayClientMessage(Component.literal("Invalid Structure"), false);
            } else {
                pPlayer.openMenu(crucible, pPos);
            }

            // Return success even if structure is invalid to prevent block placement
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrucibleControllerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null :
            createTickerHelper(blockEntityType, BlockEntitySetup.CRUCIBLE_CONTROLLER.get(), CrucibleControllerBlockEntity::serverTick);
    }

    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> typeCheck, BlockEntityType<E> desiredType, BlockEntityTicker<? super E> ticker) {
        return desiredType == typeCheck ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            pLevel.getBlockEntity(pPos, BlockEntitySetup.CRUCIBLE_CONTROLLER.get()).ifPresent(crucible -> {
                CrucibleStructure.ALL_CRUCIBLES.remove(pPos);
                BlockPos dropPos = pPos.relative(pState.getValue(FACING));
                ItemStackHandler inventory = crucible.getInventory();
                for (int slot = 0; slot < inventory.getSlots(); slot++) {
                    Containers.dropItemStack(pLevel, dropPos.getX(), dropPos.getY(), dropPos.getZ(), inventory.getStackInSlot(slot));
                }

                pLevel.updateNeighbourForOutputSignal(pPos, pState.getBlock());
                if (crucible.getStructure() != null) {
                    crucible.getStructure().getDrainPositions().forEach(drainPos -> pLevel.getBlockEntity(drainPos, BlockEntitySetup.CRUCIBLE_DRAIN.get()).ifPresent(drain -> drain.setCruciblePosition(null)));
                }
            });
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }
}
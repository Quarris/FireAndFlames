package dev.quarris.fireandflames.world.block;

import dev.quarris.fireandflames.world.block.entity.CastingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.ItemStackHandler;

public abstract class CastingBlock extends BaseEntityBlock {

    public CastingBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pRayTrace) {
        if (pLevel.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        return pLevel.getBlockEntity(pPos, this.getBlockEntityType()).map(castingBlock -> {
            int slot = castingBlock.getSlotWithItem();

            if (slot == -1) {
                return InteractionResult.PASS;
            }

            if (pPlayer.addItem(castingBlock.getInventory().getStackInSlot(slot).copy())) {
                castingBlock.getInventory().setStackInSlot(slot, ItemStack.EMPTY);
                return InteractionResult.CONSUME;
            }

            return InteractionResult.PASS;
        }).orElse(InteractionResult.FAIL);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pRayTrace) {
        if (pLevel.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
        }

        ItemStack held = pPlayer.getItemInHand(pHand);
        if (held.isEmpty()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        return pLevel.getBlockEntity(pPos, this.getBlockEntityType()).map(castingBlock -> {
            ItemStack remainder = castingBlock.getInventory().insertItem(0, held, false);
            pPlayer.setItemInHand(pHand, remainder);
            return ItemInteractionResult.CONSUME;
        }).orElse(ItemInteractionResult.FAIL);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public abstract BlockEntityType<? extends CastingBlockEntity<?>> getBlockEntityType();

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null :
            createTickerHelper(blockEntityType, this.getBlockEntityType(), CastingBlockEntity::serverTick);
    }

    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> typeCheck, BlockEntityType<E> desiredType, BlockEntityTicker<? super E> ticker) {
        return desiredType == typeCheck ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            pLevel.getBlockEntity(pPos, this.getBlockEntityType()).ifPresent(castingBlock -> {
                ItemStackHandler inventory = castingBlock.getInventory();
                for (int slot = 0; slot < inventory.getSlots(); slot++) {
                    Containers.dropItemStack(pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), inventory.getStackInSlot(slot));
                }
            });
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

}

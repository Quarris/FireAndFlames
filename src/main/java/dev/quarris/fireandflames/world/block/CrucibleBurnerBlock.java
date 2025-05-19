package dev.quarris.fireandflames.world.block;

import com.mojang.serialization.MapCodec;
import dev.quarris.fireandflames.setup.BlockEntitySetup;
import dev.quarris.fireandflames.world.block.entity.CrucibleBurnerBlockEntity;
import dev.quarris.fireandflames.world.crucible.CrucibleStructure;
import dev.quarris.fireandflames.world.inventory.menu.CrucibleBurnerMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CrucibleBurnerBlock extends BaseEntityBlock {

    public CrucibleBurnerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pRayTrace) {
        if (pLevel.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof CrucibleBurnerBlockEntity burner) {
            pPlayer.openMenu(new SimpleMenuProvider((id, inv, player) -> new CrucibleBurnerMenu(id, inv, burner), CrucibleBurnerBlockEntity.TITLE), pPos);

            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltips, TooltipFlag tooltipFlag) {
        tooltips.add(Component.translatable("block.fireandflames.crucible_burner.description").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            pLevel.getBlockEntity(pPos, BlockEntitySetup.CRUCIBLE_BURNER.get()).ifPresent(burner -> {
                ItemStackHandler inventory = burner.getInventory();
                for (int slot = 0; slot < inventory.getSlots(); slot++) {
                    Containers.dropItemStack(pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), inventory.getStackInSlot(slot));
                }

                pLevel.updateNeighbourForOutputSignal(pPos, pState.getBlock());
            });
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrucibleBurnerBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(CrucibleBurnerBlock::new);
    }
}

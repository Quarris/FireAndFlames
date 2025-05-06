package dev.quarris.fireandflames.world.block;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.setup.BlockEntitySetup;
import dev.quarris.fireandflames.setup.DataComponentSetup;
import dev.quarris.fireandflames.world.fluid.component.FluidContainerContents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;

import java.util.List;

public abstract class FluidStorageBlock extends BaseEntityBlock {

    public static final ResourceLocation CONTENTS = ModRef.res("fluid_contents");

    public FluidStorageBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pRayTrace) {
        if (FluidUtil.interactWithFluidHandler(pPlayer, pHand, pLevel, pPos, pRayTrace.getDirection())) {
            return ItemInteractionResult.sidedSuccess(pLevel.isClientSide());
        }

        return super.useItemOn(pStack, pState, pLevel, pPos, pPlayer, pHand, pRayTrace);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        int i = 0;
        int j = 0;

        for (FluidStack fluidStack : stack.getOrDefault(DataComponentSetup.FLUID_CONTAINER, FluidContainerContents.EMPTY).nonEmptyFluids()) {
            j++;
            if (i <= 4) {
                i++;
                tooltipComponents.add(Component.translatable("container.fireandflames.fluid_storage.fluid_amount", fluidStack.getHoverName(), fluidStack.getAmount()).withStyle(ChatFormatting.GRAY));
            }
        }

        if (j - i > 0) {
            tooltipComponents.add(Component.translatable("container.fireandflames.fluid_storage.more", j - i).withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        }
    }

    @Override
    protected VoxelShape getVisualShape(BlockState p_309057_, BlockGetter p_308936_, BlockPos p_308956_, CollisionContext p_309006_) {
        return Shapes.empty();
    }

    @Override
    protected float getShadeBrightness(BlockState p_308911_, BlockGetter p_308952_, BlockPos p_308918_) {
        return 1.0F;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState p_309084_, BlockGetter p_309133_, BlockPos p_309097_) {
        return true;
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return adjacentBlockState.is(this) || super.skipRendering(state, adjacentBlockState, side);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}

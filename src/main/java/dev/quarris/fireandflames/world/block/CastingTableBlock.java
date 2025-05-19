package dev.quarris.fireandflames.world.block;

import com.mojang.serialization.MapCodec;
import dev.quarris.fireandflames.setup.BlockEntitySetup;
import dev.quarris.fireandflames.world.block.entity.CastingBasinBlockEntity;
import dev.quarris.fireandflames.world.block.entity.CastingBlockEntity;
import dev.quarris.fireandflames.world.block.entity.CastingTableBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CastingTableBlock extends CastingBlock {

    private static final VoxelShape SHAPE = Shapes.or(
        Block.box(1, 12, 1, 15, 16, 15),
        Block.box(1, 0, 1, 4, 12, 4),
        Block.box(1, 0, 12, 4, 12, 15),
        Block.box(12, 0, 1, 15, 12, 4),
        Block.box(12, 0, 12, 15, 12, 15)
    );

    public CastingTableBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltips, TooltipFlag tooltipFlag) {
        tooltips.add(Component.translatable("block.fireandflames.casting_table.description").withStyle(ChatFormatting.GRAY));
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public BlockEntityType<? extends CastingBlockEntity<?>> getBlockEntityType() {
        return BlockEntitySetup.CASTING_TABLE.get();
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(CastingTableBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CastingTableBlockEntity(pPos, pState);
    }
}

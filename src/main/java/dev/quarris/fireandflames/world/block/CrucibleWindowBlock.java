package dev.quarris.fireandflames.world.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CrucibleWindowBlock extends TransparentBlock {

    public CrucibleWindowBlock(Properties pProps) {
        super(pProps);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return false;
    }
}

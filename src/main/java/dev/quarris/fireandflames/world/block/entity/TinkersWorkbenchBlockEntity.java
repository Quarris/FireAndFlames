package dev.quarris.fireandflames.world.block.entity;

import dev.quarris.fireandflames.setup.BlockEntitySetup;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TinkersWorkbenchBlockEntity extends BlockEntity {

    public static final Component TITLE = Component.translatable("container.fireandflames.tinkers_workbench.title");

    public TinkersWorkbenchBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntitySetup.TINKERS_WORKBENCH.get(), pos, blockState);
    }
}

package dev.quarris.fireandflames.world.block.entity;

import dev.quarris.fireandflames.setup.BlockEntitySetup;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidType;

public class CrucibleTankBlockEntity extends FluidStorageBlockEntity {

    public CrucibleTankBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntitySetup.CRUCIBLE_TANK.get(), pos, blockState, 4 * FluidType.BUCKET_VOLUME);
    }
}

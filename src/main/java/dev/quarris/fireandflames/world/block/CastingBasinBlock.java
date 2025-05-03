package dev.quarris.fireandflames.world.block;

import com.mojang.serialization.MapCodec;
import dev.quarris.fireandflames.setup.BlockEntitySetup;
import dev.quarris.fireandflames.world.block.entity.CastingBasinBlockEntity;
import dev.quarris.fireandflames.world.block.entity.CastingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CastingBasinBlock extends CastingBlock {

    public CastingBasinBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public BlockEntityType<? extends CastingBlockEntity<?>> getBlockEntityType() {
        return BlockEntitySetup.CASTING_BASIN.get();
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(CastingBasinBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CastingBasinBlockEntity(pPos, pState);
    }
}

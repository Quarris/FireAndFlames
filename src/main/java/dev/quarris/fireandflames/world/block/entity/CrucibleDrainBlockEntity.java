package dev.quarris.fireandflames.world.block.entity;

import dev.quarris.fireandflames.setup.BlockEntitySetup;
import dev.quarris.fireandflames.world.crucible.CrucibleFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class CrucibleDrainBlockEntity extends BlockEntity {

    private BlockPos controllerPosition;

    public CrucibleDrainBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntitySetup.CRUCIBLE_DRAIN.get(), pos, blockState);
    }

    public void setCruciblePosition(BlockPos pos) {
        this.controllerPosition = pos;
        this.invalidateCapabilities();
        this.setChanged();
        if (this.getLevel() != null) {
            this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 0);
            this.getLevel().updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        }
    }

    public Optional<CrucibleFluidTank> getCrucibleTank() {
        if (this.controllerPosition == null || this.getLevel() == null) {
            return Optional.empty();
        }

        return this.getLevel().getBlockEntity(this.controllerPosition, BlockEntitySetup.CRUCIBLE_CONTROLLER.get()).map(CrucibleControllerBlockEntity::getFluidTank);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        if (this.controllerPosition != null) {
            pTag.put("ControllerPosition", NbtUtils.writeBlockPos(this.controllerPosition));
        }
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        if (pTag.contains("ControllerPosition")) {
            this.controllerPosition = NbtUtils.readBlockPos(pTag, "ControllerPosition").orElseThrow(() -> new IllegalStateException("Could not read controller position"));
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = super.getUpdateTag(pRegistries);
        saveAdditional(tag, pRegistries);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

}

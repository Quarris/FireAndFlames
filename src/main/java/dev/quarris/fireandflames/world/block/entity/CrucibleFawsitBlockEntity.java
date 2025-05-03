package dev.quarris.fireandflames.world.block.entity;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.setup.BlockEntitySetup;
import dev.quarris.fireandflames.world.block.CrucibleFawsitBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class CrucibleFawsitBlockEntity extends BlockEntity {

    private static final int FLOW_RATE = 20;

    private BlockCapabilityCache<IFluidHandler, Direction> inputCache;
    private BlockCapabilityCache<IFluidHandler, Direction> outputCache;

    private boolean toggled;
    private FluidStack activeFluid = FluidStack.EMPTY;

    public CrucibleFawsitBlockEntity(BlockPos pPos, BlockState pState) {
        super(BlockEntitySetup.CRUCIBLE_FAWSIT.get(), pPos, pState);
    }

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, CrucibleFawsitBlockEntity pFawsit) {
        if (pFawsit.inputCache == null) {
            Direction facing = pState.getValue(CrucibleFawsitBlock.FACING);
            pFawsit.inputCache = BlockCapabilityCache.create(Capabilities.FluidHandler.BLOCK, (ServerLevel) pLevel, pPos.relative(facing.getOpposite()), facing);
        }

        if (pFawsit.outputCache == null) {
            pFawsit.outputCache = BlockCapabilityCache.create(Capabilities.FluidHandler.BLOCK, (ServerLevel) pLevel, pPos.below(), Direction.UP);
        }

        if (!pFawsit.isToggled()) {
            return;
        }

        IFluidHandler input = pFawsit.inputCache.getCapability();
        IFluidHandler output = pFawsit.outputCache.getCapability();

        if (input == null || output == null) {
            pFawsit.setToggled(false);
            return;
        }

        // Test simulation
        FluidStack simulatedDrain = input.drain(FLOW_RATE, IFluidHandler.FluidAction.SIMULATE);
        int simulatedFill = output.fill(simulatedDrain, IFluidHandler.FluidAction.SIMULATE);
        if (simulatedDrain.isEmpty() || simulatedFill <= 0) {
            pFawsit.setToggled(false);
            return;
        }

        // Execute
        FluidStack drained = input.drain(simulatedFill, IFluidHandler.FluidAction.EXECUTE);
        output.fill(drained, IFluidHandler.FluidAction.EXECUTE);
        pFawsit.setActive(drained);
    }

    private void setToggled(boolean toggledOn) {
        this.toggled = toggledOn;
        if (!toggledOn) {
            this.activeFluid = FluidStack.EMPTY;
        }
        this.setChanged();
        if (this.getLevel() != null) {
            this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 0);
        }
    }

    public void setActive(FluidStack fluid) {
        this.activeFluid = fluid;
        this.setChanged();
        if (this.getLevel() != null) {
            this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 0);
        }
    }

    public FluidStack getActiveFluid() {
        return this.activeFluid.copy();
    }

    public boolean isActive() {
        return !this.activeFluid.isEmpty();
    }

    public boolean isToggled() {
        return this.toggled;
    }

    public void toggle() {
        this.setToggled(!this.toggled);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.putBoolean("Toggled", this.toggled);
        if (!this.activeFluid.isEmpty()) {
            pTag.put("ActiveFluid", this.activeFluid.save(pRegistries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        this.toggled = pTag.getBoolean("Toggled");
        this.activeFluid = FluidStack.EMPTY;
        if (pTag.contains("ActiveFluid")) {
            this.activeFluid = FluidStack.parse(pRegistries, pTag.getCompound("ActiveFluid")).orElse(FluidStack.EMPTY);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = super.getUpdateTag(pRegistries);
        saveAdditional(tag, pRegistries);
        return tag;
    }

    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}

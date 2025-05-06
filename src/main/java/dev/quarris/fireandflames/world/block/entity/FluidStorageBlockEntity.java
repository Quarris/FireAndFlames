package dev.quarris.fireandflames.world.block.entity;

import dev.quarris.fireandflames.setup.DataComponentSetup;
import dev.quarris.fireandflames.world.fluid.component.FluidContainerContents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.LockCode;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.List;

public class FluidStorageBlockEntity extends BlockEntity {

    private final FluidTank tank;

    public FluidStorageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, int capacity) {
        super(type, pos, blockState);
        this.tank = new FluidTank(capacity) {
            @Override
            protected void onContentsChanged() {
                FluidStorageBlockEntity storage = FluidStorageBlockEntity.this;
                storage.setChanged();
                if (storage.getLevel() != null) {
                    storage.getLevel().sendBlockUpdated(storage.getBlockPos(), storage.getBlockState(), storage.getBlockState(), 0);
                }
            }
        };
    }

    public FluidStorageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        this(type, pos, blockState, FluidType.BUCKET_VOLUME);
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        componentInput.getOrDefault(DataComponentSetup.FLUID_CONTAINER, FluidContainerContents.EMPTY).consume((slot, stack) -> {
            if (slot == 0) {
                this.getFluidTank().setFluid(stack);
            }
        });
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponentSetup.FLUID_CONTAINER, FluidContainerContents.fromFluids(List.of(this.getFluidTank().getFluid())));
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.put("FluidTank", this.tank.writeToNBT(pRegistries, new CompoundTag()));
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        this.tank.readFromNBT(pRegistries, pTag.getCompound("FluidTank"));
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

    public FluidTank getFluidTank() {
        return this.tank;
    }
}

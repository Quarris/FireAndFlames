package dev.quarris.fireandflames.world.block.entity;

import dev.quarris.fireandflames.setup.BlockEntitySetup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public class CrucibleBurnerBlockEntity extends BlockEntity {

    public static final Component TITLE = Component.translatable("container.fireandflames.crucible_burner.title");

    private final ItemStackHandler inventory;

    public CrucibleBurnerBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntitySetup.CRUCIBLE_BURNER.get(), pos, blockState);
        this.inventory = new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                CrucibleBurnerBlockEntity storage = CrucibleBurnerBlockEntity.this;
                storage.setChanged();
            }
        };
    }

    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    public ItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.put("Inventory", this.inventory.serializeNBT(pRegistries));
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        this.inventory.deserializeNBT(pRegistries, pTag.getCompound("Inventory"));
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

package dev.quarris.fireandflames.world.block.entity;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.world.block.CrucibleControllerBlock;
import dev.quarris.fireandflames.world.crucible.CrucibleFluidTank;
import dev.quarris.fireandflames.world.crucible.CrucibleStructure;
import dev.quarris.fireandflames.world.inventory.menu.CrucibleMenu;
import dev.quarris.fireandflames.setup.BlockEntitySetup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.function.BiConsumer;

public class CrucibleControllerBlockEntity extends BlockEntity implements MenuProvider {

    public static final Component TITLE = Component.translatable("container.fireandflames.crucible.title");

    // Structure properties
    private CrucibleStructure crucibleStructure;

    private CrucibleFluidTank fluidTank;
    private ItemStackHandler inventory;

    // Crucible state
    private int currentHeat = 0;
    private int maxHeat = 800; // Base maximum heat level

    // Fluid storage information will be added later

    public CrucibleControllerBlockEntity(BlockPos pPos, BlockState pState) {
        super(BlockEntitySetup.CRUCIBLE_CONTROLLER.get(), pPos, pState);
        this.crucibleStructure = new CrucibleStructure(pPos);
        this.inventory = new ItemStackHandler(0);
        this.fluidTank = new CrucibleFluidTank(0);
    }

    /**
     * Server-side tick handler for the crucible controller
     */
    public static void serverTick(Level level, BlockPos pos, BlockState state, CrucibleControllerBlockEntity crucible) {
        crucible.crucibleStructure.setLevel(level);

        // Check structure
        if (level.getGameTime() % 40 == 0) {
            crucible.setChanged();
            crucible.getLevel().sendBlockUpdated(pos, state, state, 0);

        }

        if (!crucible.crucibleStructure.isStructureValid()) {
            crucible.crucibleStructure = new CrucibleStructure(pos);
            crucible.crucibleStructure.setLevel(level);
            boolean wasValid = state.getValue(CrucibleControllerBlock.LIT);
            boolean valid = crucible.crucibleStructure.validateCrucibleStructure();
            if (valid) {
                crucible.onStructureEstablished();
            }

            if (wasValid != valid) {
                level.setBlock(pos, state.setValue(CrucibleControllerBlock.LIT, valid), Block.UPDATE_ALL);
            }
        }
    }

    private ItemStackHandler createInventory() {
        return new ItemStackHandler(this.crucibleStructure.getVolume()) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return this.getStackInSlot(slot).isEmpty();
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }
        };
    }

    private CrucibleFluidTank createTank() {
        return new CrucibleFluidTank(this.crucibleStructure.getVolume()) {
            @Override
            public void onContentsChanged() {
                CrucibleControllerBlockEntity.this.setChanged();
                CrucibleControllerBlockEntity.this.getLevel().sendBlockUpdated(
                    CrucibleControllerBlockEntity.this.getBlockPos(),
                    CrucibleControllerBlockEntity.this.getBlockState(),
                    CrucibleControllerBlockEntity.this.getBlockState(),
                    0
                );
            }
        };
    }

    private void onStructureEstablished() {
        this.inventory = this.createInventory();
        this.fluidTank = this.createTank();
        this.invalidateCapabilities();
    }

    public CrucibleFluidTank getFluidTank() {
        return this.fluidTank;
    }

    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        CrucibleStructure.CODEC.encodeStart(NbtOps.INSTANCE, this.crucibleStructure)
            .resultOrPartial(ModRef.LOGGER::error)
            .ifPresent(nbt -> pTag.put("CrucibleStructure", nbt));

        pTag.put("FluidTank", this.fluidTank.serializeNBT(pRegistries));
        pTag.put("Inventory", this.inventory.serializeNBT(pRegistries));
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);

        CrucibleStructure.CODEC.parse(NbtOps.INSTANCE, pTag.get("CrucibleStructure"))
            .resultOrPartial(ModRef.LOGGER::error)
            .ifPresent(structure -> CrucibleControllerBlockEntity.this.crucibleStructure = structure);

        this.inventory = this.createInventory();
        this.fluidTank = this.createTank();
        this.invalidateCapabilities();

        this.inventory.deserializeNBT(pRegistries, pTag.getCompound("Inventory"));
        this.fluidTank.deserializeNBT(pRegistries, pTag.getCompound("FluidTank"));
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

    // Getter methods
    public boolean isStructureValid() {
        return this.crucibleStructure.isStructureValid();
    }

    public void invalidate() {
        this.crucibleStructure.invalidate();
    }

    public ItemStackHandler getInventory() {
        return this.inventory;
    }

    @Override
    public Component getDisplayName() {
        return TITLE;
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInv, Player pPlayer) {
        return new CrucibleMenu(pContainerId, pPlayerInv, this);
    }
}
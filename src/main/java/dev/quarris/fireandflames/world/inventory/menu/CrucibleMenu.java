package dev.quarris.fireandflames.world.inventory.menu;

import dev.quarris.fireandflames.network.payload.CrucibleScrollC2SPayload;
import dev.quarris.fireandflames.setup.BlockEntitySetup;
import dev.quarris.fireandflames.setup.MenuSetup;
import dev.quarris.fireandflames.world.block.entity.CrucibleControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class CrucibleMenu extends AbstractContainerMenu {

    public final Inventory playerInv;
    public final CrucibleControllerBlockEntity crucible;
    private int scroll = -1;
    private int maxScroll;

    public CrucibleMenu(int id, Inventory playerInv, BlockPos pos) {
        this(MenuSetup.CRUCIBLE.get(), id, playerInv, playerInv.player.level().getBlockEntity(pos, BlockEntitySetup.CRUCIBLE_CONTROLLER.get()).orElseThrow());
    }

    public CrucibleMenu(int id, Inventory playerInv, RegistryFriendlyByteBuf data) {
        this(id, playerInv, playerInv.player.level().getBlockEntity(data.readBlockPos(), BlockEntitySetup.CRUCIBLE_CONTROLLER.get()).orElseThrow());
    }

    public CrucibleMenu(int containerId, Inventory playerInv, CrucibleControllerBlockEntity crucible) {
        this(MenuSetup.CRUCIBLE.get(), containerId, playerInv, crucible);
    }

    public CrucibleMenu(@Nullable MenuType<CrucibleMenu> menuType, int containerId, Inventory pPlayerInv, CrucibleControllerBlockEntity crucible) {
        super(menuType, containerId);
        this.playerInv = pPlayerInv;
        this.crucible = crucible;
        this.maxScroll = Math.max(0, (int) Math.ceil((this.crucible.getInventory().getSlots() - 25) / 5.0));

        int slots = this.crucible.getInventory().getSlots();
        int slotStartX = 62;
        int slotStartY = 18;
        for (int slot = 0; slot < slots; slot++) {
            int xPos = slot % 5;
            int yPos = slot / 5;
            this.addSlot(new SlotItemHandler(this.crucible.getInventory(), slot, slotStartX + xPos * 18, slotStartY + yPos * 18) {
                @Override
                public boolean isActive() {
                    return this.index >= CrucibleMenu.this.scroll * 5 && this.index < CrucibleMenu.this.scroll * 5 + 25;
                }
            });
        }

        // Player Slots
        for (int slotY = 0; slotY < 3; slotY++) {
            for (int slotX = 0; slotX < 9; slotX++) {
                this.addSlot(new Slot(pPlayerInv, slotX + slotY * 9 + 9, 8 + slotX * 18, 127 + slotY * 18));
            }
        }

        for (int hotbar = 0; hotbar < 9; hotbar++) {
            this.addSlot(new Slot(pPlayerInv, hotbar, 8 + hotbar * 18, 185));
        }

        this.scrollTo(0);
    }

    public void scroll(int scrollY) {
        this.scrollTo(Mth.clamp(this.scroll - (int) Math.signum(scrollY), 0, this.maxScroll));
        PacketDistributor.sendToServer(new CrucibleScrollC2SPayload(this.scroll));
    }

    public void scrollTo(int scroll) {
        if (this.scroll == scroll || scroll > this.maxScroll) return;

        this.scroll = scroll;
    }


    public boolean canScroll() {
        return this.maxScroll > 0;
    }

    public double getScrollBarPosition() {
        return this.scroll / Math.max(1.0, this.maxScroll);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pSlotIndex) {
        int invSize = this.crucible.getInventory().getSlots();
        ItemStack retStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pSlotIndex);
        if (slot.hasItem()) {
            ItemStack slotItem = slot.getItem();
            retStack = slotItem.copy();
            if (pSlotIndex < invSize) {
                if (!this.moveItemStackTo(slotItem, invSize, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotItem, 0, invSize, false)) {
                return ItemStack.EMPTY;
            }

            if (slotItem.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return retStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.crucible.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        this.crucible.setSlotListener(null);
        super.removed(player);
    }

    public int getScroll() {
        return this.scroll;
    }
}

package dev.quarris.fireandflames.world.inventory.menu;

import dev.quarris.fireandflames.setup.BlockEntitySetup;
import dev.quarris.fireandflames.setup.MenuSetup;
import dev.quarris.fireandflames.world.block.entity.CrucibleBurnerBlockEntity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class CrucibleBurnerMenu extends AbstractContainerMenu {

    public final CrucibleBurnerBlockEntity burner;

    public CrucibleBurnerMenu(int id, Inventory playerInv, RegistryFriendlyByteBuf data) {
        this(id, playerInv, playerInv.player.level().getBlockEntity(data.readBlockPos(), BlockEntitySetup.CRUCIBLE_BURNER.get()).orElseThrow());
    }

    public CrucibleBurnerMenu(int containerId, Inventory playerInv, CrucibleBurnerBlockEntity burner) {
        this(MenuSetup.CRUCIBLE_BURNER.get(), containerId, playerInv, burner);
    }

    public CrucibleBurnerMenu(@Nullable MenuType<CrucibleBurnerMenu> menuType, int containerId, Inventory pPlayerInv, CrucibleBurnerBlockEntity burner) {
        super(menuType, containerId);
        this.burner = burner;

        this.addSlot(new SlotItemHandler(burner.getInventory(), 0, 80, 16));

        // Player Slots
        for (int slotY = 0; slotY < 3; slotY++) {
            for (int slotX = 0; slotX < 9; slotX++) {
                this.addSlot(new Slot(pPlayerInv, slotX + slotY * 9 + 9, 8 + slotX * 18, 63 + slotY * 18));
            }
        }

        for (int hotbar = 0; hotbar < 9; hotbar++) {
            this.addSlot(new Slot(pPlayerInv, hotbar, 8 + hotbar * 18, 121));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack retStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotItem = slot.getItem();
            retStack = slotItem.copy();
            if (index < 1) {
                if (!this.moveItemStackTo(slotItem, 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotItem, 0, 1, false)) {
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
        return this.burner.stillValid(player);
    }
}

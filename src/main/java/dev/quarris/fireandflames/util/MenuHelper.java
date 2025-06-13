package dev.quarris.fireandflames.util;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import java.util.function.Consumer;

public class MenuHelper {

    public static void addPlayerSlots(Inventory playerInventory, int x, int y, Consumer<Slot> slotAdder) {
        for (int slotY = 0; slotY < 3; slotY++) {
            for (int slotX = 0; slotX < 9; slotX++) {
                slotAdder.accept(new Slot(playerInventory, slotX + slotY * 9 + 9, x + slotX * 18, y + slotY * 18));
            }
        }

        for (int hotbar = 0; hotbar < 9; hotbar++) {
            slotAdder.accept(new Slot(playerInventory, hotbar, x + hotbar * 18, y + 58));
        }
    }

}

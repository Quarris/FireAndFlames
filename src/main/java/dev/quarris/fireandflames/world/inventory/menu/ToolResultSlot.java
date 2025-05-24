package dev.quarris.fireandflames.world.inventory.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ToolResultSlot extends Slot {

    private final OnToolCrafted onToolCrafted;

    public ToolResultSlot(Container container, int slot, int x, int y, OnToolCrafted onToolCrafted) {
        super(container, slot, x, y);
        this.onToolCrafted = onToolCrafted;
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        super.onTake(player, stack);
        this.onToolCrafted.onCraft(player);
    }

    public interface OnToolCrafted {
        void onCraft(Player player);
    }
}

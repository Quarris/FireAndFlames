package dev.quarris.fireandflames.world.inventory.menu;

import net.minecraft.world.Container;

/**
 * Names slot that doesn't call its container's {@link Container#setChanged()} function.
 * Use this only when the container already calls the function in its set methods.
 */
public class NamedContainerSlot extends NamedSlot {
    public NamedContainerSlot(String name, Container container, int slot, int x, int y) {
        super(name, container, slot, x, y);
    }

    @Override
    public void setChanged() {

    }
}

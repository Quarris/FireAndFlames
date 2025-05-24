package dev.quarris.fireandflames.world.inventory.menu;

import dev.quarris.fireandflames.util.INamed;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class NamedSlot extends Slot implements INamed {

    private final String name;
    public NamedSlot(String name, Container container, int slot, int x, int y) {
        super(container, slot, x, y);
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}

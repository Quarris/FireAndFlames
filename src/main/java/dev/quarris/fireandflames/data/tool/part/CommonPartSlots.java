package dev.quarris.fireandflames.data.tool.part;

import dev.quarris.fireandflames.setup.PartTypeSetup;
import dev.quarris.fireandflames.world.inventory.menu.SlotPosition;

public class CommonPartSlots {

    public static final PartSlot PICKAXE_HEAD = new PartSlot("pickaxe_head", PartTypeSetup.PICKAXE_HEAD, new SlotPosition(63, 50));
    public static final PartSlot AXE_HEAD = new PartSlot("axe_head", PartTypeSetup.AXE_HEAD, new SlotPosition(63, 50));
    public static final PartSlot HANDLE = new PartSlot("handle", PartTypeSetup.HANDLE, new SlotPosition(44, 69));
    public static final PartSlot GRIP = new PartSlot("grip", PartTypeSetup.HANDLE, new SlotPosition(25, 88));
    public static final PartSlot BINDING = new PartSlot("binding", PartTypeSetup.BINDING, new SlotPosition(44, 69));
    public static final PartSlot HAMMER_LEFT = new PartSlot("hammer_left", PartTypeSetup.PICKAXE_HEAD, new SlotPosition(44, 46));
    public static final PartSlot HAMMER_RIGHT = new PartSlot("hammer_right", PartTypeSetup.PICKAXE_HEAD, new SlotPosition(67, 69));

}

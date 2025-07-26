package dev.quarris.fireandflames.data.tool.part;

import dev.quarris.fireandflames.setup.PartTypeSetup;
import dev.quarris.fireandflames.world.inventory.menu.PartPosition;

public class CommonPartSlots {

    public static final PartSlot PICKAXE_HEAD = new PartSlot(1, "pickaxe_head", PartTypeSetup.PICKAXE_HEAD, 0.7f);
    public static final PartSlot AXE_HEAD = new PartSlot(1, "axe_head", PartTypeSetup.AXE_HEAD, 0.7f);
    public static final PartSlot SHOVEL_HEAD = new PartSlot(1, "shovel_head", PartTypeSetup.SHOVEL_HEAD, 0.7f);
    public static final PartSlot HOE_HEAD = new PartSlot(1, "hoe_head", PartTypeSetup.HOE_HEAD, 0.7f);
    public static final PartSlot SWORD_BLADE = new PartSlot(1, "sword_blade", PartTypeSetup.SWORD_BLADE, 0.7f);
    public static final PartSlot WIDE_GUARD = new PartSlot(2, "wide_guard", PartTypeSetup.WIDE_GUARD, 0.1f);
    public static final PartSlot HANDLE = new PartSlot(0, "handle", PartTypeSetup.HANDLE, 0.2f);
    public static final PartSlot GRIP = new PartSlot(2, "grip", PartTypeSetup.HANDLE, 0.1f);
    public static final PartSlot BINDING = new PartSlot(2, "binding", PartTypeSetup.BINDING, 0.1f);
    public static final PartSlot HAMMER_LEFT = new PartSlot(1, "hammer_left", PartTypeSetup.PICKAXE_HEAD, 0.4f);
    public static final PartSlot HAMMER_RIGHT = new PartSlot(2, "hammer_right", PartTypeSetup.PICKAXE_HEAD, 0.4f);

}

package dev.quarris.fireandflames.world.inventory.menu;

public record SlotPosition(int x, int y) {

    public SlotPosition() {
        this(0, 0);
    }

    public SlotPosition x(int x) {
        return new SlotPosition(x, this.y);
    }

    public SlotPosition y(int y) {
        return new SlotPosition(this.x, y);
    }

}

package dev.quarris.fireandflames.world.inventory.menu;

public record PartPosition(int x, int y) {

    public PartPosition() {
        this(0, 0);
    }

    public PartPosition x(int x) {
        return new PartPosition(x, this.y);
    }

    public PartPosition y(int y) {
        return new PartPosition(this.x, y);
    }

}

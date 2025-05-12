package dev.quarris.fireandflames.world.crucible.fuel;

public interface IFuelProvider {

    ActiveFuel burn(int baseTemp, IFuelConflictChecker checker);
}

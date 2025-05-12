package dev.quarris.fireandflames.world.crucible.fuel;

@FunctionalInterface
public interface IFuelConflictChecker {
    boolean conflicts(ActiveFuel fuel);
}

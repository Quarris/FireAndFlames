package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.world.crucible.fuel.IFuelProvider;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

public class CapabilitySetup {

    public static final BlockCapability<IFuelProvider, @Nullable Direction> FUEL_PROVIDER = BlockCapability.createSided(ModRef.res("fuel_provider"), IFuelProvider.class);

    public static void init() {}

}

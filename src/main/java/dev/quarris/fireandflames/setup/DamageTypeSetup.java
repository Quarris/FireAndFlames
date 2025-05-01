package dev.quarris.fireandflames.setup;

import dev.quarris.fireandflames.ModRef;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

public class DamageTypeSetup {

    public static final ResourceKey<DamageType> CRUCIBLE_MELTING_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, ModRef.res("crucible_melting"));

}

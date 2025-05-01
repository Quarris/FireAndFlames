package dev.quarris.fireandflames.datagen.server;

import dev.quarris.fireandflames.setup.DamageTypeSetup;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;

public class DamageTypeGen {

    public static void bootstrap(BootstrapContext<DamageType> ctx) {
        ctx.register(DamageTypeSetup.CRUCIBLE_MELTING_DAMAGE,
            new DamageType(
                DamageTypeSetup.CRUCIBLE_MELTING_DAMAGE.location().getPath(),
                DamageScaling.NEVER,
                0.1f,
                DamageEffects.BURNING
            ));
    }

}

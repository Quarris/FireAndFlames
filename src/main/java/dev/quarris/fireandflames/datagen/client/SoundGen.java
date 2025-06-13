package dev.quarris.fireandflames.datagen.client;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.setup.SoundSetup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class SoundGen extends SoundDefinitionsProvider {

    public SoundGen(PackOutput output, ExistingFileHelper helper) {
        super(output, ModRef.ID, helper);
    }

    @Override
    public void registerSounds() {
        this.add(SoundSetup.SMITHING_HIT, SoundDefinition.definition()
            .with(
                sound(ModRef.res("smithing_hit_0")),
                sound(ModRef.res("smithing_hit_1")),
                sound(ModRef.res("smithing_hit_2")),
                sound(ModRef.res("smithing_hit_3")),
                sound(ModRef.res("smithing_hit_4"))
            ));

        this.add(SoundSetup.SMITHING_FINAL, SoundDefinition.definition()
            .with(
                sound(ModRef.res("smithing_final"))
            ));

        this.add(SoundSetup.SMITHING_HIT_FAIL, SoundDefinition.definition()
            .with(
                sound(ModRef.res("smithing_hit_fail"))
            ));
    }
}

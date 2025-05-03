package dev.quarris.fireandflames.datagen.client;

import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.setup.BlockSetup;
import dev.quarris.fireandflames.setup.DamageTypeSetup;
import dev.quarris.fireandflames.setup.ItemSetup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class EnUsLanguageGen extends LanguageProvider {

    public EnUsLanguageGen(PackOutput output) {
        super(output, ModRef.ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add(ItemSetup.FIRE_CLAY_BALL.get(), "Fire Clay Ball");
        this.add(ItemSetup.FIRE_BRICK.get(), "Fire Brick");

        this.add(BlockSetup.FIRE_CLAY.get(), "Fire Clay");
        this.add(BlockSetup.FIRE_BRICKS.get(), "Fire Bricks");
        this.add(BlockSetup.CRUCIBLE_CONTROLLER.get(), "Crucible Controller");
        this.add(BlockSetup.CRUCIBLE_WINDOW.get(), "Crucible Window");
        this.add(BlockSetup.CRUCIBLE_DRAIN.get(), "Crucible Drain");
        this.add(BlockSetup.CRUCIBLE_FAWSIT.get(), "Crucible Fawsit (Faucet)");
        this.add(BlockSetup.CASTING_BASIN.get(), "Casting Basin");
        this.add(BlockSetup.CASTING_TABLE.get(), "Casting Table");

        this.add("container.fireandflames.crucible.title", "Crucible");
        this.add("creative_tabs.fireandflames.creative_tab", "Fire and Flames");
        this.add("death.attack.crucible_melting", "%1$s was melted by the heat of the crucible");
        this.add("death.attack.crucible_melting.player", "%1$s was thrown to the pits of the crucible by %2$s");
    }
}

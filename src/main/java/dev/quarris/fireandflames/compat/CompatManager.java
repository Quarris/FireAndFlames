package dev.quarris.fireandflames.compat;

import dev.quarris.fireandflames.compat.jade.JadeCompat;
import net.neoforged.fml.ModList;

public class CompatManager {

    public static final JadeCompat JADE_COMPAT = new JadeCompat();

    public static boolean isLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

}

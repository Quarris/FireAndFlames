package dev.quarris.fireandflames.compat;

import dev.quarris.fireandflames.compat.jade.JadeCompat;
import dev.quarris.fireandflames.compat.jei.JeiCompat;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.util.Lazy;

public class CompatManager {

    public static JadeCompat jade;
    public static JeiCompat jei;



    public static boolean isLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

}

package dev.quarris.fireandflames.compat;

import dev.quarris.fireandflames.compat.emi.EmiCompat;
import dev.quarris.fireandflames.compat.jade.JadeCompat;
import dev.quarris.fireandflames.compat.jei.JeiCompat;
import net.neoforged.fml.ModList;

public class CompatManager {

    public static JadeCompat JADE;
    public static JeiCompat JEI;
    public static EmiCompat EMI;

    public static boolean isLoaded(IModCompat compat) {
        return compat != null && ModList.get().isLoaded(compat.modId());
    }
    
    public static boolean isLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

}

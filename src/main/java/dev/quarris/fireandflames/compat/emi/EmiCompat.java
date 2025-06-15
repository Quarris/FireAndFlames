package dev.quarris.fireandflames.compat.emi;

import dev.quarris.fireandflames.compat.CompatManager;
import dev.quarris.fireandflames.compat.IModCompat;

public class EmiCompat implements IModCompat {
    public static final String MOD_ID = "emi";

    public static boolean isLoaded() {
        return CompatManager.isLoaded(MOD_ID);
    }

    //public EmiCompat() {
    //    CompatManager.EMI = this;
    //}

    @Override
    public String modId() {
        return "emi";
    }
}

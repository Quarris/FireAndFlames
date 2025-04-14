package dev.quarris.fireandflames;

import dev.quarris.fireandflames.setup.*;

public class CommonClass {
    public static void init() {
        BlockSetup.init();
        ItemSetup.init();
        BlockEntitySetup.init();
        MenuSetup.init();
    }
}

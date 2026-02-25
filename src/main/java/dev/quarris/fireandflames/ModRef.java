package dev.quarris.fireandflames;

import dev.quarris.fireandflames.data.DataManager;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModRef {

    public static final String ID = "fireandflames";
    public static final String NAME = "FireAndFlames";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public static final DataManager DATA_MANAGER = new DataManager();

    public static ResourceLocation res(String name) {
        return ResourceLocation.fromNamespaceAndPath(ID, name);
    }
}

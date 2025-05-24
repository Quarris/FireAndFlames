package dev.quarris.fireandflames.data.tool.part;

import dev.quarris.fireandflames.setup.RegistrySetup;
import net.minecraft.tags.TagKey;

public class PartType {

    public boolean is(TagKey<PartType> tag) {
        return RegistrySetup.PART_TYPES.wrapAsHolder(this).is(tag);
    }

    @Override
    public String toString() {
        return RegistrySetup.PART_TYPES.wrapAsHolder(this).getRegisteredName();
    }

}

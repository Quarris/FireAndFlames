package dev.quarris.fireandflames.data.tool.part;

import dev.quarris.fireandflames.data.tool.material.IMaterialHolder;
import net.minecraft.core.Holder;

public interface ICustomPart extends IMaterialHolder {

    Holder<PartType> getType();

}

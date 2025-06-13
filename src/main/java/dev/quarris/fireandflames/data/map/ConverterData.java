package dev.quarris.fireandflames.data.map;

import dev.quarris.fireandflames.data.tool.material.ToolMaterial;
import net.minecraft.core.Holder;

public record ConverterData(Holder<ToolMaterial> material, IMaterialConverter<?> converter) {

}

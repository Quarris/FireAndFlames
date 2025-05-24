package dev.quarris.fireandflames.data.tool;

import dev.quarris.fireandflames.data.tool.part.ToolParts;
import net.minecraft.world.item.ItemStack;

public interface ICustomTool {

    ToolType<? extends ICustomTool> getType();

    ItemStack createFrom(ToolParts parts);

    default ItemStack createFrom(ToolMaterial material) {
        return this.createFrom(this.getType().createPartsFrom(material));
    }
}

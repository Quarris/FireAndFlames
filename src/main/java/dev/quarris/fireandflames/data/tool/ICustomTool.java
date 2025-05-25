package dev.quarris.fireandflames.data.tool;

import net.minecraft.world.item.ItemStack;

public interface ICustomTool {

    ToolType<? extends ICustomTool> getType();

    ItemStack createFrom(ToolData parts);

    default ItemStack createFrom(ToolMaterial material) {
        return this.createFrom(new ToolData(this.getType().createPartsFrom(material)));
    }
}

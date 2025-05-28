package dev.quarris.fireandflames.data.tool.part;

import dev.quarris.fireandflames.data.tool.ToolMaterial;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;

public interface ICustomPart {

    Holder<PartType> getPartType();

    ItemStack createFrom(Holder<ToolMaterial> material);

    default ToolPart createPart(Holder<ToolMaterial> material) {
        return new ToolPart(this.getPartType(), material);
    }

}

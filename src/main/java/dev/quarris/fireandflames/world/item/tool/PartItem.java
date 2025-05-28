package dev.quarris.fireandflames.world.item.tool;

import dev.quarris.fireandflames.data.tool.*;
import dev.quarris.fireandflames.data.tool.part.ICustomPart;
import dev.quarris.fireandflames.data.tool.part.PartType;
import dev.quarris.fireandflames.data.tool.part.ToolPart;
import dev.quarris.fireandflames.setup.DataComponentSetup;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PartItem extends Item implements ICustomPart {

    private final Holder<PartType> partType;
    public PartItem(Holder<PartType> partType, Properties properties) {
        super(properties);
        this.partType = partType;
    }

    @Override
    public Holder<PartType> getPartType() {
        return this.partType;
    }

    @Override
    public Component getName(ItemStack stack) {
        ToolPart toolPart = stack.get(DataComponentSetup.TOOL_PART);
        String name = "You Shouldn't Have This";
        if (toolPart != null) {
            name = toolPart.material().value().name();
        }

        return Component.translatable(this.getDescriptionId(stack), name);

    }

    @Override
    public ItemStack createFrom(Holder<ToolMaterial> material) {
        ItemStack stack = new ItemStack(this);
        stack.set(DataComponentSetup.TOOL_PART, new ToolPart(this.partType, material));
        return stack;
    }
}

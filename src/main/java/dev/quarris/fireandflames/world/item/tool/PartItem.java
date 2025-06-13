package dev.quarris.fireandflames.world.item.tool;

import dev.quarris.fireandflames.data.tool.material.ToolMaterial;
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
    public Holder<PartType> getType() {
        return this.partType;
    }

    @Override
    public Component getName(ItemStack stack) {
        ToolPart toolPart = stack.get(DataComponentSetup.TOOL_PART);
        String name = "You Shouldn't Have This";
        if (toolPart != null && toolPart.material() != null) {
            name = toolPart.material().value().name();
        }

        return Component.translatable(this.getDescriptionId(stack), name);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public Holder<ToolMaterial> getMaterial(ItemStack stack) {
        return stack.has(DataComponentSetup.TOOL_PART) ? stack.get(DataComponentSetup.TOOL_PART).material() : null;
    }

    @Override
    public void setMaterial(ItemStack stack, Holder<ToolMaterial> material) {
        stack.set(DataComponentSetup.TOOL_PART, new ToolPart(material));
    }

    @Override
    public ItemStack createFrom(Holder<ToolMaterial> material) {
        ItemStack stack = new ItemStack(this);
        stack.set(DataComponentSetup.TOOL_PART, new ToolPart(material));
        return stack;
    }
}

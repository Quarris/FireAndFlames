package dev.quarris.fireandflames.world.item.tool;

import dev.quarris.fireandflames.data.tool.ICustomTool;
import dev.quarris.fireandflames.data.tool.ToolData;
import dev.quarris.fireandflames.data.tool.ToolType;
import dev.quarris.fireandflames.data.tool.part.PartSlot;
import dev.quarris.fireandflames.data.tool.part.ToolPart;
import dev.quarris.fireandflames.data.tool.part.ToolParts;
import dev.quarris.fireandflames.setup.DataComponentSetup;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

import java.util.List;
import java.util.function.Supplier;

public class HoeToolItem extends HoeItem implements ICustomTool {

    private final Supplier<ToolType<HoeToolItem>> toolType;

    public HoeToolItem(Supplier<ToolType<HoeToolItem>> toolType, Tier tier, Properties props) {
        super(tier, props);
        this.toolType = toolType;
    }

    @Override
    public ToolType<HoeToolItem> getType() {
        return this.toolType.get();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        ToolData toolData = stack.get(DataComponentSetup.TOOL_DATA);
        if (toolData == null || toolData.isEmpty()) return;

        try {
            ToolParts toolParts = toolData.toolParts();
            for (PartSlot slot : this.toolType.get().getAllSlots()) {
                ToolPart part = toolParts.getPart(slot.name());
                boolean primaryPart = this.toolType.get().mainPartName().equals(slot.name());
                tooltipComponents.add(Component.literal("[" + slot.name() + "] - " + part.material().value().name() + (primaryPart ? "(Primary)" : "")).withStyle(ChatFormatting.GRAY));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        ToolData tool = stack.get(DataComponentSetup.TOOL_DATA);
        String name = "You Shouldn't Have This";
        if (tool != null && !tool.isEmpty()) {
            name = tool.toolParts().getMainPart().material().value().name();
        }

        return Component.translatable(this.getDescriptionId(stack), name);

    }

    @Override
    public void verifyComponentsAfterLoad(ItemStack stack) {
        this.onItemLoad(stack);
    }
}

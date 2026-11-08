package dev.quarris.fireandflames.world.item.tool;

import dev.quarris.fireandflames.data.tool.ICustomTool;
import dev.quarris.fireandflames.data.tool.ToolData;
import dev.quarris.fireandflames.data.tool.ToolType;
import dev.quarris.fireandflames.setup.DataComponentSetup;
import dev.quarris.fireandflames.setup.ToolTypeSetup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class AxeToolItem extends AxeItem implements ICustomTool {

    public AxeToolItem(Tier tier, Properties props) {
        super(tier, props);
    }

    @Override
    public ToolType<AxeToolItem> getType() {
        return ToolTypeSetup.AXE.get();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        this.appendToolTooltip(stack, tooltipComponents);
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

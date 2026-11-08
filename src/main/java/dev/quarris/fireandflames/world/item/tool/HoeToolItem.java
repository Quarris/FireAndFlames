package dev.quarris.fireandflames.world.item.tool;

import dev.quarris.fireandflames.data.tool.ICustomTool;
import dev.quarris.fireandflames.data.tool.ToolData;
import dev.quarris.fireandflames.data.tool.ToolType;
import dev.quarris.fireandflames.setup.DataComponentSetup;
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

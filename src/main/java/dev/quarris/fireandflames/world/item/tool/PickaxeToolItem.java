package dev.quarris.fireandflames.world.item.tool;

import dev.quarris.fireandflames.data.tool.*;
import dev.quarris.fireandflames.setup.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.function.Supplier;

public class PickaxeToolItem extends PickaxeItem implements ICustomTool {

    private final Supplier<ToolType<PickaxeToolItem>> toolType;
    public PickaxeToolItem(Supplier<ToolType<PickaxeToolItem>> toolType, Tier tier, Properties props) {
        super(tier, props);
        this.toolType = toolType;
    }

    @Override
    public ToolType<PickaxeToolItem> getType() {
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

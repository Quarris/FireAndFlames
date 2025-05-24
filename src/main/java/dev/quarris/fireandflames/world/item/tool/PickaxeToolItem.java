package dev.quarris.fireandflames.world.item.tool;

import dev.quarris.fireandflames.data.tool.*;
import dev.quarris.fireandflames.data.tool.part.PartSlot;
import dev.quarris.fireandflames.data.tool.part.ToolPart;
import dev.quarris.fireandflames.data.tool.part.ToolParts;
import dev.quarris.fireandflames.setup.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Tool;

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
        ToolData toolData = stack.get(DataComponentSetup.TOOL_DATA);
        if (toolData == null || toolData.isEmpty()) return;

        ToolParts toolParts = toolData.toolParts();
        for (PartSlot slot : this.toolType.get().getAllSlots()) {
            ToolPart part = toolParts.getPart(slot.name());
            tooltipComponents.add(Component.literal("[" + slot.name() + "] - " + part.material().name()).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        ToolData tool = stack.get(DataComponentSetup.TOOL_DATA);
        String name = "You Shouldn't Have This";
        if (tool != null && !tool.isEmpty()) {
            name = tool.toolParts().getMainPart().material().name();
        }

        return Component.translatable(this.getDescriptionId(stack), name);

    }

    @Override
    public ItemStack createFrom(ToolParts parts) {
        ItemStack stack = new ItemStack(this);

        stack.set(DataComponents.TOOL, new Tool(List.of(
            Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_PICKAXE, 1.0f),
            Tool.Rule.deniesDrops(BlockTags.INCORRECT_FOR_WOODEN_TOOL)
        ), 1.0f, 1));

        stack.set(DataComponents.MAX_DAMAGE, 1000);
        stack.set(DataComponentSetup.TOOL_DATA, new ToolData(parts));

        return stack;
    }
}

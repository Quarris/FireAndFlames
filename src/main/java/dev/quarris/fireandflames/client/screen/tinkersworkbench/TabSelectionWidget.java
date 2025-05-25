package dev.quarris.fireandflames.client.screen.tinkersworkbench;

import com.google.common.collect.Lists;
import dev.quarris.fireandflames.ModRef;
import dev.quarris.fireandflames.client.screen.widgets.ScrollbarWidget;
import dev.quarris.fireandflames.data.tool.ICustomTool;
import dev.quarris.fireandflames.data.tool.ToolMaterial;
import dev.quarris.fireandflames.setup.MaterialSetup;
import dev.quarris.fireandflames.setup.RegistrySetup;
import dev.quarris.fireandflames.setup.ToolItemSetup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

public class TabSelectionWidget extends AbstractContainerWidget {
    public static final WidgetSprites SEARCH_BAR_SPRITES = new WidgetSprites(
        ModRef.res("container/tinkers_workbench/tool_tab_search_bar"), ModRef.res("container/tinkers_workbench/tool_tab_search_bar_focused")
    );

    public static final ResourceLocation SCROLLBAR_SPRITE = ModRef.res("container/tinkers_workbench/tool_tab_scrollbar");
    public static final ResourceLocation SCROLLBAR_FOCUSED_SPRITE = ModRef.res("container/tinkers_workbench/tool_tab_scrollbar_focused");
    public static final ResourceLocation SCROLLBAR_MARKER_SPRITE = ModRef.res("container/tinkers_workbench/tool_tab_scrollbar_marker");

    public static final WidgetSprites SCROLLBAR_SPRITES = new WidgetSprites(SCROLLBAR_SPRITE, SCROLLBAR_FOCUSED_SPRITE);
    public static final WidgetSprites SCROLLBAR_MARKER_SPRITES = new WidgetSprites(SCROLLBAR_MARKER_SPRITE, SCROLLBAR_MARKER_SPRITE);

    private final List<AbstractWidget> widgets = Lists.newArrayList();
    private final List<TabSelectionButton> selectionButtons = Lists.newArrayList();
    private final List<TabSelection> allOptions;
    private List<TabSelection> filteredOptions;
    private final EditBox searchBar;

    private int scroll;
    private int maxToolCount;
    public ScrollbarWidget scrollbar;
    private final OnToolTypeSelected onToolTypeSelected;

    public TabSelectionWidget(int x, int y, int width, int height, OnToolTypeSelected onToolTypeSelected) {
        super(x, y, width, height, CommonComponents.EMPTY);
        this.onToolTypeSelected = onToolTypeSelected;

        ToolMaterial goldMaterial = Minecraft.getInstance().level.registryAccess().registryOrThrow(RegistrySetup.Keys.MATERIALS).getOrThrow(MaterialSetup.GOLD);
        this.allOptions = new ArrayList<>();
        this.allOptions.add(new TabSelection(ModRef.res("upgrade"), "Upgrade"::contains, new TabSelectionButton.StackTabRenderer(new ItemStack(Items.REDSTONE))));

            ToolItemSetup.REGISTRY.getEntries().stream()
                .filter(item -> item.get() instanceof ICustomTool)
                .map(i -> ((ICustomTool) i.get()))
                .sorted(((Comparator<ICustomTool>) (tool1, tool2) -> {
                    int ordering1 = tool1.getType().ordering();
                    int ordering2 = tool2.getType().ordering();
                    if (ordering1 < 0 && ordering2 < 0) return 0;
                    if (ordering1 < 0) return 1;
                    if (ordering2 < 0) return -1;
                    return 0;
                }).thenComparingInt(tool -> tool.getType().ordering()))
                .map(tool -> {
                    ResourceLocation toolName = RegistrySetup.TOOL_TYPES.getKey(tool.getType());
                    return new TabSelection(toolName, toolName.getPath().toLowerCase(Locale.ROOT)::contains, new TabSelectionButton.StackTabRenderer(tool.createFrom(goldMaterial)));
                })
                .forEach(this.allOptions::add);
        this.filteredOptions = new ArrayList<>(this.allOptions);

        this.searchBar = new EditBox(Minecraft.getInstance().font, x + 6, y + 5, width - 12, 14, Component.literal("Filter tool")) {
            @Override
            protected boolean isValidClickButton(int button) {
                return super.isValidClickButton(button) || button == 1;
            }

            @Override
            public void onClick(double mouseX, double mouseY, int button) {
                if (button == 1) {
                    this.setValue("");
                    return;
                }

                super.onClick(mouseX, mouseY, button);
            }
        };
        this.searchBar.setBordered(false);
        this.searchBar.setResponder(this::onFilter);
        this.widgets.add(this.searchBar);

        this.scrollbar = new ScrollbarWidget(SCROLLBAR_SPRITES, SCROLLBAR_MARKER_SPRITES, this.getX() + 4, this.getY() + 22, 10, this.getHeight() - 24, this::onScroll);
        this.widgets.add(this.scrollbar);

        this.setToolCount(this.allOptions.size());
        this.refreshOptions(this.allOptions);
    }

    private void onScroll(int scroll) {
        this.scroll = scroll;
        this.refreshOptions(this.filteredOptions);
    }

    private void setToolCount(int count) {
        int toolSelectionHeight = this.getHeight() - 20;
        int toolCountLimit = toolSelectionHeight / 22;
        this.maxToolCount = Math.min(count, toolCountLimit);
        this.scrollbar.updateMaxScroll(this.maxToolCount - toolCountLimit);
    }

    private void onFilter(String filter) {
        this.filteredOptions.clear();
        this.allOptions.stream()
            .filter(option -> option.filter.test(filter))
            .forEach(this.filteredOptions::add);

        this.setToolCount(this.filteredOptions.size());
        this.refreshOptions(this.filteredOptions);
    }

    public void refreshOptions(List<TabSelection> options) {
        this.selectionButtons.clear();
        for (int i = 0; i < this.maxToolCount; i++) {
            TabSelection selection = options.get(this.scroll + i);
            this.selectionButtons.add(new TabSelectionButton(selection.name(), this.getX() + 18, this.getY() + 20 + i * 22, 20, 20, selection.iconRenderer(), this.onToolTypeSelected));
        }
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Search Bar Background
        ResourceLocation searchSprite = SEARCH_BAR_SPRITES.get(this.searchBar.isActive(), this.searchBar.isFocused());
        guiGraphics.blitSprite(searchSprite, this.searchBar.getX() - 4, this.searchBar.getY() - 3, this.searchBar.getWidth() + 8, this.searchBar.getHeight());

        for (AbstractWidget widget : this.children()) {
            widget.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            return false;
        }

        if (this.searchBar.canConsumeInput()) {
            return this.searchBar.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
        }

        if (this.scrollbar.isFocused()) {
            return this.scrollbar.keyPressed(keyCode, scanCode, modifiers);
        }

        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void setFocused(boolean focused) {
        if (!focused) {
            this.setFocused(null);
        }
    }

    @Override
    public void setFocused(@Nullable GuiEventListener widget) {
        super.setFocused(widget);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public Rect2i getSize() {
        return new Rect2i(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    @Override
    public List<AbstractWidget> children() {
        ArrayList<AbstractWidget> children = new ArrayList<>(this.widgets);
        children.addAll(this.selectionButtons);
        return children;
    }

    public interface OnToolTypeSelected {
        void select(ResourceLocation tabName);
    }

    public record TabSelection(ResourceLocation name, Predicate<String> filter, TabSelectionButton.TabIconRenderer iconRenderer) {

    }
}

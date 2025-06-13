package dev.quarris.fireandflames.compat.jei;

import dev.quarris.fireandflames.client.screen.TinkersWorkbenchScreen;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.renderer.Rect2i;

import java.util.ArrayList;
import java.util.List;

public class TinkersWorkbenchGuiHandler implements IGuiContainerHandler<TinkersWorkbenchScreen> {

    @Override
    public List<Rect2i> getGuiExtraAreas(TinkersWorkbenchScreen screen) {
        List<Rect2i> areas = new ArrayList<>(1);
        if (screen.tabSelection != null) {
            areas.add(screen.tabSelection.getSize());
        }

        return areas;
    }
}

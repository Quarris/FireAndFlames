package dev.quarris.fireandflames.data;

import dev.quarris.fireandflames.data.tool.modifier.ToolModifierManager;

public class DataManager {

    private ToolModifierManager toolModifierManager;

    public DataManager() {

    }

    public void init() {
        this.toolModifierManager = new ToolModifierManager();
    }

    public ToolModifierManager getToolModifiers() {
        return toolModifierManager;
    }
}

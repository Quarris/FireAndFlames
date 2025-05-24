package dev.quarris.fireandflames.client.screen.widgets;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import org.jetbrains.annotations.Nullable;

public class DefaultedEditBox extends EditBox {

    private String defaultValue = "";

    public DefaultedEditBox(Font font, int width, int height, Component message) {
        super(font, width, height, message);
    }

    public DefaultedEditBox(Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, message);
    }

    public DefaultedEditBox(Font font, int x, int y, int width, int height, @Nullable EditBox editBox, Component message) {
        super(font, x, y, width, height, editBox, message);
        if (editBox instanceof DefaultedEditBox defaultedEditBox) {
            this.defaultValue = defaultedEditBox.defaultValue;
            this.setValue(editBox.getValue());
        }
    }

    public void setDefaultValue(String defaultValue) {
        if (defaultValue == null || this.defaultValue.equals(defaultValue)) {
            return;
        }

        this.defaultValue = defaultValue;
        this.setValue(this.getValue());
    }

    @Override
    public void setValue(String text) {
        if (StringUtil.isBlank(text) && !StringUtil.isBlank(this.defaultValue)) {
            super.setValue(this.defaultValue);
            return;
        }

        super.setValue(text);
    }
}

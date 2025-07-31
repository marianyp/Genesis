package dev.mariany.genesis.client.gui.widget;

import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;

public class ToggleableRecipeBookWidget extends TexturedButtonWidget {
    public ToggleableRecipeBookWidget(int x, int y, PressAction pressAction) {
        super(x, y, 20, 18, RecipeBookWidget.BUTTON_TEXTURES, pressAction);
    }

    public void setEnabled(boolean value) {
        this.active = value;
        this.visible = value;
    }
}

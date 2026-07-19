package dev.mariany.genesis.client.gui.widget;

import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;

public class ToggleableRecipeBookWidget extends ImageButton {
    public ToggleableRecipeBookWidget(int x, int y, OnPress pressAction) {
        super(x, y, 20, 18, RecipeBookComponent.RECIPE_BUTTON_SPRITES, pressAction);
    }

    public void setEnabled(boolean value) {
        this.active = value;
        this.visible = value;
    }
}

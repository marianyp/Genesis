package dev.mariany.genesis.client.gui.screen;

import dev.mariany.genesis.client.event.RecipeBookScreenEvents;
import dev.mariany.genesis.client.gui.screen.ingame.AssemblyScreen;
import dev.mariany.genesis.client.gui.widget.ToggleableRecipeBookWidget;
import dev.mariany.genesis.mixin.accessor.AbstractContainerScreenAccessor;
import dev.mariany.genesis.mixin.accessor.AbstractRecipeBookScreenAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;

@Environment(EnvType.CLIENT)
public final class AssemblyRecipeBookHandler {
    private AssemblyRecipeBookHandler() {
    }

    public static void bootstrap() {
        RecipeBookScreenEvents.MODIFY_BUTTON.register(AssemblyRecipeBookHandler::modifyButton);
    }

    private static AbstractWidget modifyButton(AbstractRecipeBookScreen<?> screen, AbstractWidget button) {
        if (!(screen instanceof AssemblyScreen)) {
            return button;
        }

        AbstractRecipeBookScreenAccessor recipeBookScreen = (AbstractRecipeBookScreenAccessor) screen;
        AbstractContainerScreenAccessor containerScreen = (AbstractContainerScreenAccessor) screen;
        ScreenPosition position = recipeBookScreen.genesis$getRecipeBookButtonPosition();

        return new ToggleableRecipeBookWidget(position.x(), position.y(), toggleButton -> {
            RecipeBookComponent<?> recipeBook = recipeBookScreen.genesis$recipeBookComponent();
            recipeBook.toggleVisibility();

            int leftPos = recipeBook.updateScreenPosition(screen.width, containerScreen.genesis$imageWidth());
            containerScreen.genesis$setLeftPos(leftPos);

            ScreenPosition buttonPosition = recipeBookScreen.genesis$getRecipeBookButtonPosition();
            toggleButton.setPosition(buttonPosition.x(), buttonPosition.y());
            recipeBookScreen.genesis$onRecipeBookButtonClick();
        });
    }
}

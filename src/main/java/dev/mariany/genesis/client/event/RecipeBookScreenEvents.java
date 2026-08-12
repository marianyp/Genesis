package dev.mariany.genesis.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;

public final class RecipeBookScreenEvents {
    public static final Event<ModifyButton> MODIFY_BUTTON = EventFactory.createArrayBacked(
            ModifyButton.class,
            callbacks -> (screen, button) -> {
                for (ModifyButton callback : callbacks) {
                    button = callback.modify(screen, button);
                }

                return button;
            }
    );

    private RecipeBookScreenEvents() {
    }

    @FunctionalInterface
    public interface ModifyButton {
        AbstractWidget modify(AbstractRecipeBookScreen<?> screen, AbstractWidget button);
    }
}

package dev.mariany.genesis.compat.jei.event;

import dev.mariany.genesis.compat.jei.recipe.display.adapter.JEIRecipeDisplayAdapter;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.List;
import java.util.function.Consumer;

public final class JEIEvents {
    /**
     * Orders the recipe categories displayed by JEI.
     */
    public static final Event<OrderRecipeCategories> ORDER_RECIPE_CATEGORIES = EventFactory.createArrayBacked(
            OrderRecipeCategories.class,
            callbacks -> recipeCategories -> {
                List<IRecipeCategory<?>> orderedRecipeCategories = recipeCategories;

                for (OrderRecipeCategories callback : callbacks) {
                    orderedRecipeCategories = callback.order(orderedRecipeCategories);
                }

                return orderedRecipeCategories;
            }
    );

    /**
     * Collects prioritized recipe category identifiers in descending preference order.
     */
    public static final Event<PrioritizeRecipeDisplay> PRIORITIZE_RECIPE_DISPLAY = EventFactory.createArrayBacked(
            PrioritizeRecipeDisplay.class,
            callbacks -> categoryConsumer -> {
                for (PrioritizeRecipeDisplay callback : callbacks) {
                    callback.prioritize(categoryConsumer);
                }
            }
    );

    private JEIEvents() {
    }

    @FunctionalInterface
    public interface PrioritizeRecipeDisplay {
        void prioritize(Consumer<JEIRecipeDisplayAdapter<?>> consumer);
    }

    @FunctionalInterface
    public interface OrderRecipeCategories {
        List<IRecipeCategory<?>> order(List<IRecipeCategory<?>> recipeCategories);
    }
}

package dev.mariany.genesis.event.recipe;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.crafting.RecipeMap;

public final class RecipeEvents {
    public static final Event<ModifyRecipes> MODIFY_RECIPES = EventFactory.createArrayBacked(
            ModifyRecipes.class,
            callbacks -> recipes -> {
                RecipeMap modifiedRecipes = recipes;

                for (ModifyRecipes callback : callbacks) {
                    modifiedRecipes = callback.modify(modifiedRecipes);
                }

                return modifiedRecipes;
            }
    );

    private RecipeEvents() {
    }

    @FunctionalInterface
    public interface ModifyRecipes {
        RecipeMap modify(RecipeMap recipes);
    }
}

package dev.mariany.genesis.recipe.display;

import dev.mariany.genesis.recipe.display.type.RecipeDisplayType;
import dev.mariany.genesis.recipe.display.type.RecipeDisplayTypes;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeDisplayRegistry {
    private final Map<RecipeDisplayType<?>, List<? extends IdentifiedRecipeDisplay>> displays = new HashMap<>();

    public void refresh(Collection<RecipeHolder<?>> recipes) {
        RecipeDisplayTypes.all().forEach(type -> this.refresh(type, recipes));
    }

    private <D extends IdentifiedRecipeDisplay> void refresh(
            RecipeDisplayType<D> type,
            Collection<RecipeHolder<?>> recipes
    ) {
        this.displays.put(type, type.provide(recipes));
    }

    public <D extends IdentifiedRecipeDisplay> List<D> get(RecipeDisplayType<D> type) {
        List<? extends IdentifiedRecipeDisplay> displays = this.displays.getOrDefault(type, List.of());
        return displays.stream().map(type.displayClass()::cast).toList();
    }
}

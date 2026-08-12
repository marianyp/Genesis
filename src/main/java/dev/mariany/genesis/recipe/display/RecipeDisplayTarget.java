package dev.mariany.genesis.recipe.display;

import java.util.function.Supplier;

public record RecipeDisplayTarget<S>(
        Supplier<Class<S>> screenClass,
        int x,
        int y,
        int width,
        int height
) {
    public RecipeDisplayTarget {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Recipe display click areas must have a positive size");
        }
    }
}

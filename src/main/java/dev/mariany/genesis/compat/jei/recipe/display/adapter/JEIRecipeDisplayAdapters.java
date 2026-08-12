package dev.mariany.genesis.compat.jei.recipe.display.adapter;

import dev.mariany.genesis.compat.jei.client.category.JEIAssemblyRecipeCategory;
import dev.mariany.genesis.recipe.display.IdentifiedRecipeDisplay;
import dev.mariany.genesis.recipe.display.type.RecipeDisplayType;
import dev.mariany.genesis.recipe.display.type.RecipeDisplayTypes;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class JEIRecipeDisplayAdapters {
    private static final List<JEIRecipeDisplayAdapter<?>> ALL = new ArrayList<>();

    static {
        register(RecipeDisplayTypes.ASSEMBLY, JEIAssemblyRecipeCategory::new);
        register(RecipeDisplayTypes.SIFTING);
    }

    private JEIRecipeDisplayAdapters() {
    }

    private static <T extends IdentifiedRecipeDisplay> void register(RecipeDisplayType<T> displayType) {
        register(displayType, null);
    }

    private static <T extends IdentifiedRecipeDisplay> void register(
            RecipeDisplayType<T> displayType,
            JEIRecipeDisplayAdapter.@Nullable CategoryFactory<T> categoryFactory
    ) {
        register(new JEIRecipeDisplayAdapter<>(displayType, categoryFactory));
    }

    private static void register(JEIRecipeDisplayAdapter<?> adapter) {
        ALL.add(adapter);
    }

    public static List<JEIRecipeDisplayAdapter<?>> all() {
        return List.copyOf(ALL);
    }
}

package dev.mariany.genesis.compat.jei.client.category;

import dev.mariany.genesis.compat.jei.event.JEIEvents;
import dev.mariany.genesis.recipe.display.type.RecipeDisplayOrdering;
import dev.mariany.genesis.recipe.display.type.RecipeDisplayType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JEIRecipeDisplayOrdering extends RecipeDisplayOrdering<IRecipeCategory<?>, Identifier> {
    public JEIRecipeDisplayOrdering() {
        super(BuiltInRegistries.RECIPE_TYPE::getKey);
    }

    public void bootstrap() {
        JEIEvents.ORDER_RECIPE_CATEGORIES.register(this::sortByPriority);
    }

    @Override
    protected List<IRecipeCategory<?>> sortByPriority(List<IRecipeCategory<?>> recipeCategories) {
        Map<Identifier, RecipeDisplayType.Priority> prioritizedCategories = new LinkedHashMap<>();

        JEIEvents.PRIORITIZE_RECIPE_DISPLAY
                .invoker()
                .prioritize(adapter -> prioritizedCategories.putIfAbsent(
                        adapter.displayType().category().id(),
                        adapter.displayType().priority()
                ));

        if (recipeCategories.size() <= 1 || prioritizedCategories.isEmpty()) {
            return recipeCategories;
        }

        Map<Identifier, IRecipeCategory<?>> categoriesById = new LinkedHashMap<>();

        recipeCategories.forEach(category -> categoriesById.put(
                category.getRecipeType().getUid(),
                category
        ));

        List<Identifier> preferredCategoryIds = this.sort(
                categoriesById.keySet(),
                categoriesById.keySet(),
                prioritizedCategories
        );

        List<IRecipeCategory<?>> preferredCategories = preferredCategoryIds
                .stream()
                .<IRecipeCategory<?>>map(categoriesById::get)
                .toList();

        return preferredCategories.equals(recipeCategories)
                ? recipeCategories
                : preferredCategories;
    }
}

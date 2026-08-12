package dev.mariany.genesis.compat.jei.recipe.display;

import dev.mariany.genesis.compat.jei.recipe.display.adapter.JEIRecipeDisplayAdapter;
import dev.mariany.genesis.compat.jei.recipe.display.adapter.JEIRecipeDisplayAdapters;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.registration.*;

import java.util.List;

public final class JEIRecipeDisplays {
    private static final List<JEIRecipeDisplayAdapter<?>> ALL = JEIRecipeDisplayAdapters.all();

    private JEIRecipeDisplays() {
    }

    public static void registerCategoriesAndPriorities(IRecipeCategoryRegistration registration) {
        registerCategories(registration);
        registerPriorities();
    }

    private static void registerCategories(IRecipeCategoryRegistration registration) {
        ALL.forEach(adapter -> adapter.registerCategory(registration));
    }

    private static void registerPriorities() {
        ALL.forEach(JEIRecipeDisplayAdapter::registerPriority);
    }

    public static void registerRecipes(IRecipeRegistration registration) {
        ALL.forEach(adapter -> adapter.registerRecipes(registration));
    }

    public static void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        ALL.forEach(adapter -> adapter.registerRecipeCatalysts(registration));
    }

    public static void registerGuiHandlers(IGuiHandlerRegistration registration) {
        ALL.forEach(adapter -> adapter.registerGuiHandlers(registration));
    }

    public static void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        ALL.forEach(adapter -> adapter.registerRecipeTransferHandler(registration));
    }

    public static void refreshRecipes(IRecipeManager recipeManager) {
        ALL.forEach(adapter -> adapter.refreshRecipes(recipeManager));
    }
}

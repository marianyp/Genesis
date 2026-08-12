package dev.mariany.genesis.compat.jei;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.block.GenesisBlocks;
import dev.mariany.genesis.client.GenesisClient;
import dev.mariany.genesis.compat.jei.client.category.JEIRecipeDisplayOrdering;
import dev.mariany.genesis.compat.jei.recipe.display.JEIRecipeDisplays;
import dev.mariany.genesis.logic.AssemblyRecipeLogic;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@JeiPlugin
public final class GenesisJEIPlugin implements IModPlugin {
    private static final Identifier PLUGIN_ID = Genesis.id("jei_plugin");

    private final JEIRecipeDisplayOrdering recipeDisplayOrdering = new JEIRecipeDisplayOrdering();
    private final Runnable refreshListener = this::refreshRecipes;

    private final List<RecipeHolder<CraftingRecipe>> hiddenRecipes = new ArrayList<>();

    @Nullable
    private IJeiRuntime runtime;

    @Override
    public Identifier getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        this.recipeDisplayOrdering.bootstrap();
        JEIRecipeDisplays.registerCategoriesAndPriorities(registration);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        JEIRecipeDisplays.registerRecipes(registration);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(RecipeTypes.SMELTING, GenesisBlocks.KILN);
        JEIRecipeDisplays.registerRecipeCatalysts(registration);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        JEIRecipeDisplays.registerGuiHandlers(registration);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        JEIRecipeDisplays.registerRecipeTransferHandlers(registration);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        this.runtime = jeiRuntime;
        GenesisClient.RECIPE_DISPLAY_REGISTRY.addRefreshListener(this.refreshListener);
        this.refreshRecipes();
    }

    @Override
    public void onRuntimeUnavailable() {
        GenesisClient.RECIPE_DISPLAY_REGISTRY.removeRefreshListener(this.refreshListener);
        this.hiddenRecipes.clear();
        this.runtime = null;
    }

    private void refreshRecipes() {
        this.getRecipeManager().ifPresent(recipeManager -> {
            JEIRecipeDisplays.refreshRecipes(recipeManager);
            this.refreshHiddenRecipes(recipeManager);
        });
    }

    private Optional<IRecipeManager> getRecipeManager() {
        return Optional.ofNullable(this.runtime).map(IJeiRuntime::getRecipeManager);
    }

    private void refreshHiddenRecipes(IRecipeManager recipeManager) {
        this.removeHiddenRecipes(recipeManager);

        this.hiddenRecipes.addAll(collectHiddenRecipes(recipeManager));

        this.hideRecipes(recipeManager);
    }

    private void removeHiddenRecipes(IRecipeManager recipeManager) {
        if (this.hiddenRecipes.isEmpty()) {
            return;
        }

        recipeManager.unhideRecipes(RecipeTypes.CRAFTING, this.hiddenRecipes);

        this.hiddenRecipes.clear();
    }

    private static List<RecipeHolder<CraftingRecipe>> collectHiddenRecipes(IRecipeManager recipeManager) {
        return collectAssemblyRecipeConverts(recipeManager);
    }

    private static List<RecipeHolder<CraftingRecipe>> collectAssemblyRecipeConverts(IRecipeManager recipeManager) {
        Set<ResourceKey<Recipe<?>>> assemblyRecipeKeys = AssemblyRecipeLogic
                .createAssemblyRecipesFrom(GenesisClient.RECIPE_DISPLAY_REGISTRY.recipes())
                .stream()
                .map(RecipeHolder::id)
                .collect(Collectors.toUnmodifiableSet());

        return recipeManager
                .createRecipeLookup(RecipeTypes.CRAFTING)
                .includeHidden()
                .get()
                .filter(recipe -> assemblyRecipeKeys.contains(recipe.id()))
                .toList();
    }

    private void hideRecipes(IRecipeManager recipeManager) {
        if (this.hiddenRecipes.isEmpty()) {
            return;
        }

        recipeManager.hideRecipes(RecipeTypes.CRAFTING, this.hiddenRecipes);
    }
}

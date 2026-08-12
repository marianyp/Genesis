package dev.mariany.genesis.compat.jei.recipe.display.adapter;

import dev.mariany.genesis.client.GenesisClient;
import dev.mariany.genesis.compat.jei.client.category.JEIRecipeCategory;
import dev.mariany.genesis.compat.jei.client.transfer.JEIRecipeTransferHandler;
import dev.mariany.genesis.compat.jei.event.JEIEvents;
import dev.mariany.genesis.recipe.display.IdentifiedRecipeDisplay;
import dev.mariany.genesis.recipe.display.RecipeDisplayTarget;
import dev.mariany.genesis.recipe.display.RecipeDisplayTransfer;
import dev.mariany.genesis.recipe.display.type.RecipeDisplayType;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class JEIRecipeDisplayAdapter<D extends IdentifiedRecipeDisplay> {
    private final RecipeDisplayType<D> displayType;
    private final IRecipeType<D> recipeType;
    private final CategoryFactory<D> categoryFactory;

    private List<D> registeredRecipes = List.of();

    public JEIRecipeDisplayAdapter(
            RecipeDisplayType<D> displayType,
            @Nullable CategoryFactory<D> categoryFactory
    ) {
        this.displayType = displayType;
        this.recipeType = IRecipeType.create(displayType.category().id(), displayType.displayClass());
        this.categoryFactory = categoryFactory == null ? JEIRecipeCategory::new : categoryFactory;
    }

    public RecipeDisplayType<D> displayType() {
        return this.displayType;
    }

    public void registerCategory(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(this.categoryFactory.create(
                registration.getJeiHelpers().getGuiHelper(),
                this.recipeType,
                this.displayType
        ));
    }

    public void registerRecipes(IRecipeRegistration registration) {
        this.registeredRecipes = GenesisClient.RECIPE_DISPLAY_REGISTRY.get(this.displayType);
        registration.addRecipes(this.recipeType, this.registeredRecipes);
    }

    public void refreshRecipes(IRecipeManager recipeManager) {
        List<D> recipes = GenesisClient.RECIPE_DISPLAY_REGISTRY.get(this.displayType);

        if (recipes.equals(this.registeredRecipes)) {
            return;
        }

        if (!this.registeredRecipes.isEmpty()) {
            recipeManager.hideRecipes(this.recipeType, this.registeredRecipes);
        }

        recipeManager.addRecipes(this.recipeType, recipes);

        this.registeredRecipes = recipes;
    }

    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        this.displayType
                .category()
                .workstations()
                .forEach(workstation -> registration.addCraftingStation(
                        this.recipeType,
                        workstation.get()
                ));
    }

    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        this.displayType
                .targets()
                .forEach(target -> registration.addRecipeClickArea(
                        screenClass(target),
                        target.x(),
                        target.y(),
                        target.width(),
                        target.height(),
                        this.recipeType
                ));
    }

    public void registerRecipeTransferHandler(IRecipeTransferRegistration registration) {
        this.displayType.transfer().ifPresent(transfer -> registerRecipeTransferHandler(
                registration,
                transfer
        ));
    }

    private <M extends AbstractContainerMenu> void registerRecipeTransferHandler(
            IRecipeTransferRegistration registration,
            RecipeDisplayTransfer<D, M> transfer
    ) {
        registration.addRecipeTransferHandler(
                new JEIRecipeTransferHandler<>(this.recipeType, transfer, registration.getTransferHelper()),
                this.recipeType
        );
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends AbstractContainerScreen<?>> screenClass(RecipeDisplayTarget<?> target) {
        return (Class<? extends AbstractContainerScreen<?>>) target
                .screenClass()
                .get()
                .asSubclass(AbstractContainerScreen.class);
    }

    public void registerPriority() {
        if (this.displayType.priority().isUnprioritized()) {
            return;
        }

        JEIEvents.PRIORITIZE_RECIPE_DISPLAY
                .register(consumer -> consumer.accept(this));
    }

    @FunctionalInterface
    public interface CategoryFactory<D extends IdentifiedRecipeDisplay> {
        IRecipeCategory<D> create(
                IGuiHelper guiHelper,
                IRecipeType<D> recipeType,
                RecipeDisplayType<D> displayType
        );
    }
}

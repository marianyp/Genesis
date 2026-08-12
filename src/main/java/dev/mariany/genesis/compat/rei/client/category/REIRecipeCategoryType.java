package dev.mariany.genesis.compat.rei.client.category;

import dev.mariany.genesis.compat.rei.display.adapter.REIRecipeDisplayAdapter;
import me.shedaniel.rei.plugin.common.displays.crafting.CraftingDisplay;
import org.jspecify.annotations.Nullable;

public record REIRecipeCategoryType<D extends CraftingDisplay>(
        REIRecipeDisplayAdapter<D> displayAdapter,
        CategoryFactory<D> categoryFactory
) {
    public REIRecipeCategoryType(
            REIRecipeDisplayAdapter<D> displayAdapter,
            @Nullable CategoryFactory<D> categoryFactory
    ) {
        this.displayAdapter = displayAdapter;
        this.categoryFactory = categoryFactory == null ? REIRecipeCategory::new : categoryFactory;
    }

    public REIRecipeCategory<D> createCategory() {
        return this.categoryFactory.create(this.displayAdapter);
    }

    @FunctionalInterface
    public interface CategoryFactory<D extends CraftingDisplay> {
        REIRecipeCategory<D> create(REIRecipeDisplayAdapter<D> adapter);
    }
}

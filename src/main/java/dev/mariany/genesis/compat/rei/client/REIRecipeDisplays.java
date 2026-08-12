package dev.mariany.genesis.compat.rei.client;

import dev.mariany.genesis.compat.rei.client.category.REIRecipeCategoryType;
import dev.mariany.genesis.compat.rei.client.category.REIRecipeCategoryTypes;
import dev.mariany.genesis.compat.rei.display.adapter.REIRecipeDisplayAdapter;
import dev.mariany.genesis.compat.rei.display.adapter.REIRecipeDisplayAdapters;
import dev.mariany.genesis.recipe.display.RecipeDisplayTarget;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandlerRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.displays.crafting.CraftingDisplay;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public final class REIRecipeDisplays {
    private REIRecipeDisplays() {
    }

    public static void registerCategories(CategoryRegistry registry) {
        REIRecipeCategoryTypes
                .all()
                .forEach(type -> registerCategory(registry, type));
    }

    private static <D extends CraftingDisplay> void registerCategory(
            CategoryRegistry registry,
            REIRecipeCategoryType<D> categoryType
    ) {
        registry.add(categoryType.createCategory());
        registerWorkstations(registry, categoryType.displayAdapter());
    }

    private static <D extends CraftingDisplay> void registerWorkstations(
            CategoryRegistry registry,
            REIRecipeDisplayAdapter<D> adapter
    ) {
        adapter
                .displayType()
                .category()
                .workstations()
                .forEach(workstation ->
                                 registry.addWorkstations(
                                         adapter.categoryIdentifier(),
                                         EntryStacks.of(workstation.get())
                                 )
                );
    }

    public static void registerScreens(ScreenRegistry registry) {
        REIRecipeDisplayAdapters
                .all()
                .forEach(adapter -> adapter
                        .displayType()
                        .targets()
                        .forEach(target -> registerScreen(
                                registry,
                                target,
                                adapter.categoryIdentifier()
                        ))
                );
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void registerScreen(
            ScreenRegistry registry,
            RecipeDisplayTarget<?> target,
            CategoryIdentifier<?> categoryIdentifier
    ) {
        Class<? extends AbstractContainerScreen> screenClass = target
                .screenClass()
                .get()
                .asSubclass(AbstractContainerScreen.class);

        registry.registerContainerClickArea(
                new Rectangle(target.x(), target.y(), target.width(), target.height()),
                screenClass,
                categoryIdentifier
        );
    }

    public static void registerTransferHandlers(TransferHandlerRegistry registry) {
        REIRecipeDisplayAdapters
                .all()
                .forEach(adapter -> adapter.registerTransferHandler(
                        registry
                ));
    }
}

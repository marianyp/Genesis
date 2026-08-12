package dev.mariany.genesis.compat.rei.client.category;

import dev.mariany.genesis.compat.rei.display.adapter.REIRecipeDisplayAdapter;
import dev.mariany.genesis.compat.rei.display.adapter.REIRecipeDisplayAdapters;
import me.shedaniel.rei.plugin.common.displays.crafting.CraftingDisplay;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class REIRecipeCategoryTypes {
    private static final List<REIRecipeCategoryType<?>> ALL = new ArrayList<>();

    static {
        register(REIRecipeDisplayAdapters.ASSEMBLY, REIAssemblyRecipeCategory::new);
        register(REIRecipeDisplayAdapters.SIFTING);
    }

    private REIRecipeCategoryTypes() {
    }

    private static <T extends CraftingDisplay> void register(
            REIRecipeDisplayAdapter<T> displayAdapter
    ) {
        register(displayAdapter, null);
    }

    private static <T extends CraftingDisplay> void register(
            REIRecipeDisplayAdapter<T> displayAdapter,
            REIRecipeCategoryType.@Nullable CategoryFactory<T> categoryFactory
    ) {
        register(new REIRecipeCategoryType<>(displayAdapter, categoryFactory));
    }

    private static void register(REIRecipeCategoryType<?> type) {
        ALL.add(type);
    }

    public static List<REIRecipeCategoryType<?>> all() {
        return List.copyOf(ALL);
    }
}

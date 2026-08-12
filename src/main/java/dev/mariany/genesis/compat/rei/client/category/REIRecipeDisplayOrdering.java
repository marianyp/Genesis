package dev.mariany.genesis.compat.rei.client.category;

import dev.mariany.genesis.compat.rei.display.adapter.REIRecipeDisplayAdapters;
import dev.mariany.genesis.recipe.display.type.RecipeDisplayOrdering;
import dev.mariany.genesis.recipe.display.type.RecipeDisplayType;
import me.shedaniel.rei.api.client.config.ConfigObject;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeType;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class REIRecipeDisplayOrdering extends RecipeDisplayOrdering<CategoryIdentifier<?>, CategoryIdentifier<?>> {
    public REIRecipeDisplayOrdering() {
        super(REIRecipeDisplayOrdering::getCategoryIdentifier);
    }

    @Nullable
    private static CategoryIdentifier<?> getCategoryIdentifier(RecipeType<?> recipeType) {
        Identifier recipeTypeId = BuiltInRegistries.RECIPE_TYPE.getKey(recipeType);
        return recipeTypeId == null ? null : CategoryIdentifier.of(recipeTypeId.withPrefix("plugins/"));
    }

    public void apply(CategoryRegistry registry) {
        List<CategoryIdentifier<?>> registeredCategoryIds = new ArrayList<>();

        registry.forEach(configuration -> registeredCategoryIds.add(
                configuration.getCategoryIdentifier()
        ));

        List<CategoryIdentifier<?>> categoryOrdering = getCategoryOrdering();
        List<CategoryIdentifier<?>> updatedOrdering = this.sortByPriority(registeredCategoryIds);

        if (categoryOrdering.equals(updatedOrdering)) {
            return;
        }

        categoryOrdering.clear();
        categoryOrdering.addAll(updatedOrdering);
    }

    @Override
    protected List<CategoryIdentifier<?>> sortByPriority(List<CategoryIdentifier<?>> registeredCategoryIds) {
        Map<CategoryIdentifier<?>, RecipeDisplayType.Priority> prioritizedCategories = new LinkedHashMap<>();

        REIRecipeDisplayAdapters.all().forEach(adapter -> {
            RecipeDisplayType.Priority priority = adapter.displayType().priority();

            if (priority.isUnprioritized()) {
                return;
            }

            prioritizedCategories.putIfAbsent(adapter.categoryIdentifier(), priority);
        });

        List<CategoryIdentifier<?>> categoryOrdering = getCategoryOrdering();

        return this.sort(registeredCategoryIds, categoryOrdering, prioritizedCategories);
    }

    @SuppressWarnings("UnstableApiUsage")
    private static List<CategoryIdentifier<?>> getCategoryOrdering() {
        return ConfigObject.getInstance().getCategoryOrdering();
    }
}

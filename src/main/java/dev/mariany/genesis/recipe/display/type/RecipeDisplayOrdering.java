package dev.mariany.genesis.recipe.display.type;

import net.minecraft.world.item.crafting.RecipeType;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public abstract class RecipeDisplayOrdering<T, C> {
    private final Function<RecipeType<?>, @Nullable C> anchorResolver;

    public RecipeDisplayOrdering(Function<RecipeType<?>, @Nullable C> anchorResolver) {
        this.anchorResolver = anchorResolver;
    }

    protected abstract List<T> sortByPriority(List<T> elements);

    protected List<C> sort(
            Collection<C> availableCategoryIds,
            Collection<C> categoryOrdering,
            Map<C, RecipeDisplayType.Priority> prioritizedCategories
    ) {
        if (prioritizedCategories.isEmpty()) {
            return List.copyOf(categoryOrdering);
        }

        Set<C> preferredCategoryIds = new LinkedHashSet<>();
        Map<C, PriorityGroup<C>> relativePriorityGroups = new LinkedHashMap<>();

        prioritizedCategories.forEach((anchor, priority) -> {
            if (!availableCategoryIds.contains(anchor)) {
                return;
            }

            switch (priority.placement()) {
                case NONE -> {
                }
                case FIRST -> preferredCategoryIds.add(anchor);
                case BEFORE, AFTER -> this.addRelativePriority(
                        anchor,
                        priority,
                        availableCategoryIds,
                        preferredCategoryIds,
                        relativePriorityGroups
                );
            }
        });

        relativePriorityGroups.forEach((anchorId, group) -> {
            preferredCategoryIds.addAll(group.before);
            preferredCategoryIds.add(anchorId);
            preferredCategoryIds.addAll(group.after);
        });

        preferredCategoryIds.addAll(categoryOrdering);

        return List.copyOf(preferredCategoryIds);
    }

    private void addRelativePriority(
            C categoryId,
            RecipeDisplayType.Priority priority,
            Collection<C> availableCategoryIds,
            Set<C> preferredCategoryIds,
            Map<C, PriorityGroup<C>> relativePriorityGroups
    ) {
        RecipeType<?> anchor = priority.anchor();

        C anchorId = anchor == null
                ? null
                : this.anchorResolver.apply(anchor);

        if (anchorId == null || !availableCategoryIds.contains(anchorId)) {
            preferredCategoryIds.add(categoryId);
            return;
        }

        PriorityGroup<C> group = relativePriorityGroups.computeIfAbsent(anchorId, _ -> new PriorityGroup<>());

        if (priority.placement() == RecipeDisplayType.Priority.Placement.BEFORE) {
            group.before.add(categoryId);
        } else {
            group.after.add(categoryId);
        }
    }

    private static final class PriorityGroup<C> {
        private final List<C> before = new ArrayList<>();
        private final List<C> after = new ArrayList<>();
    }
}

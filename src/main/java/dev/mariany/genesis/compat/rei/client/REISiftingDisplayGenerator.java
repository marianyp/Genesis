package dev.mariany.genesis.compat.rei.client;

import dev.mariany.genesis.compat.rei.display.REISiftingDisplay;
import dev.mariany.genesis.recipe.display.type.RecipeDisplayTypes;
import me.shedaniel.rei.api.client.registry.display.DynamicDisplayGenerator;
import me.shedaniel.rei.api.client.view.ViewSearchBuilder;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;

import java.util.List;
import java.util.Optional;

public class REISiftingDisplayGenerator implements DynamicDisplayGenerator<REISiftingDisplay> {
    @Override
    public Optional<List<REISiftingDisplay>> getRecipeFor(EntryStack<?> entry) {
        return matchingDisplays(entry, false);
    }

    @Override
    public Optional<List<REISiftingDisplay>> getUsageFor(EntryStack<?> entry) {
        return matchingDisplays(entry, true);
    }

    @Override
    public Optional<List<REISiftingDisplay>> generate(ViewSearchBuilder builder) {
        List<REISiftingDisplay> displays = displays();

        if (!builder.getRecipesFor().isEmpty()) {
            displays = displays
                    .stream()
                    .filter(display -> builder
                            .getRecipesFor()
                            .stream()
                            .anyMatch(entry -> matches(display.getOutputEntries(), entry))
                    )
                    .toList();
        }

        if (!builder.getUsagesFor().isEmpty()) {
            displays = displays
                    .stream()
                    .filter(display -> builder
                            .getUsagesFor()
                            .stream()
                            .anyMatch(entry -> matches(display.getInputEntries(), entry))
                    )
                    .toList();
        }

        return displays.isEmpty() ? Optional.empty() : Optional.of(displays);
    }

    private static Optional<List<REISiftingDisplay>> matchingDisplays(
            EntryStack<?> entry,
            boolean input
    ) {
        List<REISiftingDisplay> displays = displays()
                .stream()
                .filter(display -> matches(
                        input ? display.getInputEntries() : display.getOutputEntries(),
                        entry
                ))
                .toList();

        return displays.isEmpty() ? Optional.empty() : Optional.of(displays);
    }

    private static boolean matches(List<EntryIngredient> ingredients, EntryStack<?> entry) {
        return ingredients.stream()
                          .flatMap(List::stream)
                          .anyMatch(candidate -> EntryStacks.equalsFuzzy(candidate, entry));
    }

    private static List<REISiftingDisplay> displays() {
        return RecipeDisplayTypes.SIFTING
                .provide(List.of())
                .stream()
                .map(REISiftingDisplay::new)
                .toList();
    }
}

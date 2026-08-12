package dev.mariany.genesis.recipe.display;

import java.util.List;

public record RecipeDisplayContents<T>(
        List<T> additionalInputs,
        List<T> craftingInputs,
        List<T> outputs
) {
    public RecipeDisplayContents {
        additionalInputs = List.copyOf(additionalInputs);
        craftingInputs = List.copyOf(craftingInputs);
        outputs = List.copyOf(outputs);
    }
}

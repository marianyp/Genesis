package dev.mariany.genesis.compat.rei;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.compat.rei.display.AssemblyDisplay;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;

public class REICategoryIdentifiers {
    public static final CategoryIdentifier<AssemblyDisplay> ASSEMBLY = of("plugins/assembly");

    private static <D extends Display> CategoryIdentifier<D> of(String path) {
        return CategoryIdentifier.of(Genesis.id(path));
    }
}

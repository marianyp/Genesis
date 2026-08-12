package dev.mariany.genesis.recipe.display;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.function.Supplier;

public record RecipeDisplayCategory(
        Identifier id,
        String titleTranslationKey,
        int width,
        int height,
        Supplier<? extends ItemLike> icon,
        List<Supplier<? extends ItemLike>> workstations,
        RecipeDisplayLayout layout
) {
    public RecipeDisplayCategory(
            Identifier id,
            String titleTranslationKey,
            int width,
            int height,
            Supplier<? extends ItemLike> icon,
            RecipeDisplayLayout layout
    ) {
        this(id, titleTranslationKey, width, height, icon, List.of(icon), layout);
    }

    public RecipeDisplayCategory {
        workstations = List.copyOf(workstations);
    }
}

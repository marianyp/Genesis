package dev.mariany.genesis.recipe.display;

import dev.mariany.genesis.Genesis;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.display.RecipeDisplay;

public final class GenesisRecipeDisplays {
    private GenesisRecipeDisplays() {
    }

    public static void bootstrap() {
        Genesis.bootstrapLog("Recipe Displays");
        register("assembly", AssemblyCraftingRecipeDisplay.SERIALIZER);
        register("sifting", SiftingRecipeDisplay.SERIALIZER);
    }

    private static void register(String name, RecipeDisplay.Type<?> type) {
        Registry.register(BuiltInRegistries.RECIPE_DISPLAY, Genesis.id(name), type);
    }
}

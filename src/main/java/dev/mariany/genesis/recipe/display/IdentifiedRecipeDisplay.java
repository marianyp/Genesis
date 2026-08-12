package dev.mariany.genesis.recipe.display;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.display.RecipeDisplay;

public interface IdentifiedRecipeDisplay extends RecipeDisplay {
    Identifier identifier();
}

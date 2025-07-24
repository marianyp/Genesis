package dev.mariany.genesis.recipe.brew;

import net.minecraft.item.Item;

public record BrewItemRecipe(Item from, Item ingredient, Item to) {
}

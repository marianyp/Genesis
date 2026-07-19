package dev.mariany.genesis.recipe.brew;

import net.minecraft.world.item.Item;

public record BrewItemRecipe(Item from, Item ingredient, Item to) {
}

package dev.mariany.genesis.datagen;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.item.GenesisItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.BrewingRecipe;
import net.minecraft.world.item.crafting.PotionIngredient;
import net.minecraft.world.item.crafting.Recipe;

public final class GenesisBrewingProvider {
    private final RecipeOutput output;

    public GenesisBrewingProvider(RecipeOutput output) {
        this.output = output;
    }

    public void buildRecipes() {
        this.brewing(Items.HONEY_BOTTLE, Items.GLISTERING_MELON_SLICE, GenesisItems.ENCHANTED_HONEY_BOTTLE);
        this.brewing(Items.HONEY_BOTTLE, Items.FERMENTED_SPIDER_EYE, Items.OMINOUS_BOTTLE);
    }

    private void brewing(Item input, Item reagent, Item result) {
        Identifier resultId = BuiltInRegistries.ITEM.getKey(result);

        ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(
                Registries.RECIPE,
                Genesis.id("brewing/" + resultId.getPath())
        );

        BrewingRecipe recipe = new BrewingRecipe(
                PotionIngredient.of(input),
                PotionIngredient.of(reagent),
                new ItemStackTemplate(result)
        );

        this.output.accept(recipeKey, recipe, null);
    }
}

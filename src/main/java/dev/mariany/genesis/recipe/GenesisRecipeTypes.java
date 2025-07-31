package dev.mariany.genesis.recipe;

import dev.mariany.genesis.Genesis;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class GenesisRecipeTypes {
    public static final RecipeType<CraftingRecipe> ASSEMBLY = register("assembly");

    private static <T extends Recipe<?>> RecipeType<T> register(String id) {
        return Registry.register(Registries.RECIPE_TYPE, Genesis.id(id), new RecipeType<T>() {
            public String toString() {
                return id;
            }
        });
    }

    public static void bootstrap() {
        Genesis.LOGGER.info("Registering Recipe Types for " + Genesis.MOD_ID);
    }
}

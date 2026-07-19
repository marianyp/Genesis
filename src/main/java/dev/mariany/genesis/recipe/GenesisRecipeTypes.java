package dev.mariany.genesis.recipe;

import dev.mariany.genesis.Genesis;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public class GenesisRecipeTypes {
    public static final RecipeType<CraftingRecipe> ASSEMBLY = register("assembly");

    private static <T extends Recipe<?>> RecipeType<T> register(String id) {
        return Registry.register(BuiltInRegistries.RECIPE_TYPE, Genesis.id(id), new RecipeType<T>() {
            public String toString() {
                return id;
            }
        });
    }

    public static void bootstrap() {
        Genesis.bootstrapLog("Recipe Types");
    }
}

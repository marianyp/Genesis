package dev.mariany.genesis.recipe;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.tag.GenesisTags;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.PreparedRecipes;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AssemblyRecipeProvider {
    private final RegistryWrapper.WrapperLookup wrapperLookup;

    public AssemblyRecipeProvider(RegistryWrapper.WrapperLookup wrapperLookup) {
        this.wrapperLookup = wrapperLookup;
    }

    public PreparedRecipes provide(Collection<RecipeEntry<?>> oldRecipes) {
        List<RecipeEntry<?>> newRecipes = new ArrayList<>();

        int assemblyRecipeCount = 0;

        for (RecipeEntry<?> entry : oldRecipes) {
            Recipe<?> recipe = entry.value();

            if (recipe instanceof ShapedRecipe shapedRecipe) {
                if (getShapedRecipeResult(shapedRecipe).isIn(GenesisTags.Items.FROM_ASSEMBLY_TABLE)) {
                    newRecipes.add(createAssemblyRecipe(entry.id(), shapedRecipe));
                    ++assemblyRecipeCount;
                    continue;
                }
            }

            newRecipes.add(entry);
        }

        Genesis.LOGGER.info("Created {} assembly recipes successfully!", assemblyRecipeCount);

        return PreparedRecipes.of(newRecipes);
    }

    private ItemStack getShapedRecipeResult(ShapedRecipe shapedRecipe) {
        return shapedRecipe.craft(CraftingRecipeInput.EMPTY, this.wrapperLookup);
    }

    private RecipeEntry<?> createAssemblyRecipe(RegistryKey<Recipe<?>> id, ShapedRecipe oldRecipe) {
        return new RecipeEntry<>(id, new AssemblyRecipe(oldRecipe));
    }
}

package dev.mariany.genesis.recipe;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.item.custom.AssemblyPatternItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.PreparedRecipes;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class DynamicAssemblyRecipeProvider {
    private final RegistryWrapper.WrapperLookup wrapperLookup;

    public DynamicAssemblyRecipeProvider(RegistryWrapper.WrapperLookup wrapperLookup) {
        this.wrapperLookup = wrapperLookup;
    }

    public PreparedRecipes provide(Collection<RecipeEntry<?>> oldRecipes) {
        List<AssemblyPatternItem> patterns = getPatterns();
        List<RecipeEntry<?>> newRecipes = new ArrayList<>();

        int assemblyRecipeCount = 0;

        for (RecipeEntry<?> entry : oldRecipes) {
            Recipe<?> recipe = entry.value();

            if (recipe instanceof ShapedRecipe shapedRecipe) {
                List<AssemblyPatternItem> validPatterns = new ArrayList<>();

                for (AssemblyPatternItem pattern : patterns) {
                    if (getShapedRecipeResult(shapedRecipe).isIn(pattern.getCrafts())) {
                        validPatterns.add(pattern);
                    }
                }

                if (!validPatterns.isEmpty()) {
                    newRecipes.add(createAssemblyRecipe(entry.id(), shapedRecipe, validPatterns));
                    ++assemblyRecipeCount;
                    continue;
                }
            }

            newRecipes.add(entry);
        }

        Genesis.LOGGER.info("Created {} assembly recipes successfully!", assemblyRecipeCount);

        return PreparedRecipes.of(newRecipes);
    }

    private static List<AssemblyPatternItem> getPatterns() {
        return Registries.ITEM.stream()
                .map(item ->
                        item instanceof AssemblyPatternItem assemblyPatternItem
                                ? assemblyPatternItem
                                : null
                )
                .filter(Objects::nonNull)
                .toList();
    }

    private ItemStack getShapedRecipeResult(ShapedRecipe shapedRecipe) {
        return shapedRecipe.craft(CraftingRecipeInput.EMPTY, this.wrapperLookup);
    }

    private RecipeEntry<?> createAssemblyRecipe(
            RegistryKey<Recipe<?>> id,
            ShapedRecipe oldRecipe,
            List<AssemblyPatternItem> patterns
    ) {
        return new RecipeEntry<>(id, new AssemblyRecipe(oldRecipe, patterns));
    }
}

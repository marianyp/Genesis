package dev.mariany.genesis.recipe;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.item.custom.AssemblyPatternItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.*;

public class DynamicAssemblyRecipeProvider {
    public DynamicAssemblyRecipeProvider() {
    }

    public RecipeMap provide(Collection<RecipeHolder<?>> oldRecipes) {
        List<AssemblyPatternItem> patterns = getPatterns();
        List<RecipeHolder<?>> newRecipes = new ArrayList<>();

        int assemblyRecipeCount = 0;

        for (RecipeHolder<?> entry : oldRecipes) {
            Recipe<?> recipe = entry.value();

            if (recipe instanceof ShapedRecipe shapedRecipe) {
                List<AssemblyPatternItem> validPatterns = new ArrayList<>();

                for (AssemblyPatternItem pattern : patterns) {
                    if (getShapedRecipeResult(shapedRecipe).is(pattern.getCrafts())) {
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

        return RecipeMap.create(newRecipes);
    }

    private static List<AssemblyPatternItem> getPatterns() {
        return BuiltInRegistries.ITEM
                .stream()
                .map(DynamicAssemblyRecipeProvider::asAssemblyPatternItem)
                .flatMap(Optional::stream)
                .toList();
    }

    private static Optional<AssemblyPatternItem> asAssemblyPatternItem(Item item) {
        return item instanceof AssemblyPatternItem assemblyPatternItem
                ? Optional.of(assemblyPatternItem)
                : Optional.empty();
    }

    private ItemStack getShapedRecipeResult(ShapedRecipe shapedRecipe) {
        return shapedRecipe.assemble(CraftingInput.EMPTY);
    }

    private RecipeHolder<?> createAssemblyRecipe(
            ResourceKey<Recipe<?>> id,
            ShapedRecipe oldRecipe,
            List<AssemblyPatternItem> patterns
    ) {
        return new RecipeHolder<>(id, new AssemblyRecipe(oldRecipe, patterns));
    }
}

package dev.mariany.genesis.logic;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.event.recipe.RecipeEvents;
import dev.mariany.genesis.item.custom.AssemblyPatternItem;
import dev.mariany.genesis.recipe.AssemblyRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class AssemblyRecipeLogic {
    private AssemblyRecipeLogic() {
    }

    public static void bootstrap() {
        Genesis.bootstrapLog("Assembly Recipe Logic");
        RecipeEvents.MODIFY_RECIPES.register(AssemblyRecipeLogic::provide);
    }

    private static RecipeMap provide(RecipeMap recipes) {
        Collection<RecipeHolder<?>> oldRecipes = recipes.values();

        Map<ResourceKey<Recipe<?>>, RecipeHolder<AssemblyRecipe>> assemblyRecipes =
                createAssemblyRecipesFrom(oldRecipes)
                        .stream()
                        .collect(Collectors.toMap(RecipeHolder::id, recipe -> recipe));

        List<RecipeHolder<?>> newRecipes = oldRecipes
                .stream()
                .<RecipeHolder<?>>map(recipe -> {
                    RecipeHolder<AssemblyRecipe> replacement = assemblyRecipes.get(recipe.id());
                    return replacement != null ? replacement : recipe;
                })
                .toList();

        assemblyRecipes
                .values()
                .stream()
                .filter(recipe -> !recipe.value().isPossible())
                .forEach(recipe -> Genesis.LOGGER.warn(
                        "Found impossible assembly recipe '{}': no compatible pattern shape for recipe",
                        recipe.id().identifier()
                ));

        Genesis.LOGGER.info("Created {} assembly recipes", assemblyRecipes.size());

        return RecipeMap.create(newRecipes);
    }

    public static List<RecipeHolder<AssemblyRecipe>> createAssemblyRecipesFrom(
            Collection<RecipeHolder<?>> oldRecipes
    ) {
        List<AssemblyPatternItem> patterns = getPatterns();
        List<RecipeHolder<AssemblyRecipe>> recipes = new ArrayList<>();

        for (RecipeHolder<?> entry : oldRecipes) {
            if (!(entry.value() instanceof ShapedRecipe shapedRecipe)) {
                continue;
            }

            List<AssemblyPatternItem> validPatterns = patterns
                    .stream()
                    .filter(pattern -> getShapedRecipeResult(shapedRecipe).is(pattern.getCrafts()))
                    .toList();

            if (!validPatterns.isEmpty()) {
                recipes.add(createAssemblyRecipe(entry.id(), shapedRecipe, validPatterns));
            }
        }

        return recipes;
    }

    private static List<AssemblyPatternItem> getPatterns() {
        return BuiltInRegistries.ITEM
                .stream()
                .map(AssemblyRecipeLogic::asAssemblyPatternItem)
                .flatMap(Optional::stream)
                .toList();
    }

    private static Optional<AssemblyPatternItem> asAssemblyPatternItem(Item item) {
        return item instanceof AssemblyPatternItem assemblyPatternItem
                ? Optional.of(assemblyPatternItem)
                : Optional.empty();
    }

    private static ItemStack getShapedRecipeResult(ShapedRecipe shapedRecipe) {
        return shapedRecipe.assemble(CraftingInput.EMPTY);
    }

    private static RecipeHolder<AssemblyRecipe> createAssemblyRecipe(
            ResourceKey<Recipe<?>> recipeResourceKey,
            ShapedRecipe oldRecipe,
            List<AssemblyPatternItem> patterns
    ) {
        return new RecipeHolder<>(
                recipeResourceKey,
                new AssemblyRecipe(recipeResourceKey.identifier(), oldRecipe, patterns)
        );
    }
}

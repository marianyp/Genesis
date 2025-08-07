package dev.mariany.genesis.recipe;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.item.GenesisItems;
import dev.mariany.genesis.tag.GenesisTags;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.PreparedRecipes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DynamicHealthyStewRecipeProvider {
    private final RegistryWrapper.WrapperLookup wrapperLookup;

    public DynamicHealthyStewRecipeProvider(RegistryWrapper.WrapperLookup wrapperLookup) {
        this.wrapperLookup = wrapperLookup;
    }

    public static List<Identifier> getRecipesInvolving(Item item) {
        return getRecipesInvolving(item, getItemsInTag(GenesisTags.Items.HEALTHY_STEW_CONTENTS));
    }

    private static List<Identifier> getRecipesInvolving(Item item, List<Item> possibleIngredients) {
        return generate(possibleIngredients).stream()
                .filter(data -> data.ingredients().contains(item))
                .map(HealthyStewRecipeData::id)
                .toList();
    }

    private static List<HealthyStewRecipeData> generate(List<Item> possibleIngredients) {
        List<HealthyStewRecipeData> results = new ArrayList<>();
        int ingredientCount = possibleIngredients.size();

        for (int i = 0; i < ingredientCount; i++) {
            Item primaryItem = possibleIngredients.get(i);
            for (int j = i + 1; j < ingredientCount; j++) {
                Item secondaryItem = possibleIngredients.get(j);

                // Create recipe using 2 of primaryItem and 1 of secondaryItem
                Identifier idA = createStewRecipeId(primaryItem, secondaryItem);
                List<Item> ingredientsA = List.of(primaryItem, primaryItem, secondaryItem);
                results.add(new HealthyStewRecipeData(idA, primaryItem, ingredientsA));

                // Create recipe using 2 of secondaryItem and 1 of primaryItem
                Identifier idB = createStewRecipeId(secondaryItem, primaryItem);
                List<Item> ingredientsB = List.of(primaryItem, secondaryItem, secondaryItem);
                results.add(new HealthyStewRecipeData(idB, secondaryItem, ingredientsB));
            }
        }

        return results;
    }

    public PreparedRecipes provide(Collection<RecipeEntry<?>> recipes) {
        List<RecipeEntry<?>> newRecipes = new ArrayList<>(recipes);

        this.wrapperLookup.getOptional(RegistryKeys.ITEM)
                .flatMap(itemRegistry -> itemRegistry.getOptional(GenesisTags.Items.HEALTHY_STEW_CONTENTS))
                .ifPresent(entryList -> {
                    List<Item> itemsInTag = entryList.stream()
                            .map(RegistryEntry::value)
                            .toList();

                    List<HealthyStewRecipeData> recipeDataList = generate(itemsInTag);

                    for (HealthyStewRecipeData data : recipeDataList) {
                        newRecipes.add(new RecipeEntry<>(
                                RegistryKey.of(RegistryKeys.RECIPE, data.id()),
                                createStewRecipe(data)
                        ));
                    }

                    Genesis.LOGGER.info("Added {} healthy stew recipes!", recipeDataList.size());
                });

        return PreparedRecipes.of(newRecipes);
    }

    private ShapelessRecipe createStewRecipe(HealthyStewRecipeData data) {
        List<Item> ingredients = new ArrayList<>(data.ingredients());
        ingredients.add(Items.BOWL);

        List<Ingredient> ingredientList = ingredients.stream()
                .map(Ingredient::ofItem)
                .toList();

        String group = "healthy_stew_";
        group += data.primaryItem().getRegistryEntry().registryKey().getValue().getPath();

        return new ShapelessRecipe(
                group,
                CraftingRecipeCategory.MISC,
                GenesisItems.HEALTHY_STEW.getDefaultStack(),
                ingredientList
        );
    }

    private static Identifier createStewRecipeId(Item twoItem, Item oneItem) {
        String twoName = twoItem.getRegistryEntry().registryKey().getValue().getPath();
        String oneName = oneItem.getRegistryEntry().registryKey().getValue().getPath();
        String pattern = "2_" + twoName + "_1_" + oneName;
        return Genesis.id("healthy_stew/" + pattern);
    }

    private static List<Item> getItemsInTag(TagKey<Item> tag) {
        return Registries.ITEM.getOptional(tag).map(registryEntries -> registryEntries.stream()
                .map(RegistryEntry::value)
                .toList()).orElseGet(List::of);

    }

    public record HealthyStewRecipeData(Identifier id, Item primaryItem, List<Item> ingredients) {
    }
}

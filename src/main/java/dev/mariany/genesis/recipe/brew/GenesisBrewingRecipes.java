package dev.mariany.genesis.recipe.brew;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.item.GenesisItems;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class GenesisBrewingRecipes {
    private static final List<BrewItemRecipe> brewItemRecipes = new ArrayList<>();

    static {
        add(Items.HONEY_BOTTLE, Items.GLISTERING_MELON_SLICE, GenesisItems.ENCHANTED_HONEY_BOTTLE);
        add(Items.HONEY_BOTTLE, Items.FERMENTED_SPIDER_EYE, Items.OMINOUS_BOTTLE);
    }

    private static void add(Item from, Item ingredient, Item to) {
        brewItemRecipes.add(new BrewItemRecipe(from, ingredient, to));
    }

    public static List<BrewItemRecipe> getBrewItemRecipes() {
        return brewItemRecipes.stream().toList();
    }

    public static List<Item> getPotionBypasses() {
        return brewItemRecipes.stream().flatMap(brewItemRecipe -> Stream.of(
                        brewItemRecipe.from(),
                        brewItemRecipe.to()
                )
        ).toList();
    }

    public static void registerBrewingRecipes() {
        Genesis.LOGGER.info("Registering Brewing Recipes for " + Genesis.MOD_ID);

        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> brewItemRecipes.forEach(
                        brewItemRecipe -> builder.registerItemRecipe(
                                brewItemRecipe.from(),
                                brewItemRecipe.ingredient(),
                                brewItemRecipe.to()
                        )
                )
        );
    }
}

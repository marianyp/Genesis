package dev.mariany.genesis.recipe.brew;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.item.GenesisItems;
import net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class GenesisBrewingRecipes {
    private static final List<BrewItemRecipe> RECIPES = new ArrayList<>();

    static {
        add(Items.HONEY_BOTTLE, Items.GLISTERING_MELON_SLICE, GenesisItems.ENCHANTED_HONEY_BOTTLE);
        add(Items.HONEY_BOTTLE, Items.FERMENTED_SPIDER_EYE, Items.OMINOUS_BOTTLE);
    }

    private static void add(Item from, Item ingredient, Item to) {
        RECIPES.add(new BrewItemRecipe(from, ingredient, to));
    }

    public static List<BrewItemRecipe> getRecipes() {
        return RECIPES.stream().toList();
    }

    public static List<Item> getPotionBypasses() {
        return RECIPES.stream().flatMap(brewItemRecipe -> Stream.of(
                                                brewItemRecipe.from(),
                                                brewItemRecipe.to()
                                        )
        ).toList();
    }

    public static void bootstrap() {
        Genesis.bootstrapLog("Brewing Recipes");

        FabricPotionBrewingBuilder.BUILD.register(
                builder -> RECIPES.forEach(
                        brewItemRecipe -> builder.addContainerRecipe(
                                brewItemRecipe.from(),
                                brewItemRecipe.ingredient(),
                                brewItemRecipe.to()
                        )
                )
        );
    }
}

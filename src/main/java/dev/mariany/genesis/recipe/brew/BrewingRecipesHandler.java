package dev.mariany.genesis.recipe.brew;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.event.brewing.BrewingEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.jetbrains.annotations.Nullable;

public final class BrewingRecipesHandler {
    private BrewingRecipesHandler() {
    }

    public static void bootstrap() {
        Genesis.bootstrapLog("Custom Brewing Logic");
        BrewingEvents.ALLOW_INPUT.register(BrewingRecipesHandler::onAllowInput);
        BrewingEvents.ALLOW_INGREDIENT.register(BrewingRecipesHandler::isIngredient);
        BrewingEvents.MIX.register(BrewingRecipesHandler::mix);
    }

    private static boolean onAllowInput(BrewingStandBlockEntity brewingStandBlockEntity, int slot, ItemStack stack) {
        if (slot == 3 || slot == 4) {
            return false;
        }

        return brewingStandBlockEntity.getItem(slot).isEmpty() && isIngredient(stack.getItem());
    }

    private static boolean isIngredient(Item item) {
        return GenesisBrewingRecipes.getIngredients().contains(item);
    }

    @Nullable
    private static ItemStack mix(ItemStack ingredient, ItemStack input) {
        for (BrewItemRecipe recipe : GenesisBrewingRecipes.getRecipes()) {
            if (input.is(recipe.from()) && ingredient.is(recipe.ingredient())) {
                return recipe.to().getDefaultInstance();
            }
        }

        return null;
    }
}

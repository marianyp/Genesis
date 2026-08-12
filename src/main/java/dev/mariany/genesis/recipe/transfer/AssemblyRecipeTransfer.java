package dev.mariany.genesis.recipe.transfer;

import dev.mariany.genesis.logic.AssemblyRecipeLogic;
import dev.mariany.genesis.recipe.display.AssemblyCraftingRecipeDisplay;
import dev.mariany.genesis.screen.AssemblyScreenHandler;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public final class AssemblyRecipeTransfer {
    private AssemblyRecipeTransfer() {
    }

    public static Optional<ResourceKey<Recipe<?>>> findRecipeKey(
            AssemblyCraftingRecipeDisplay display,
            Collection<RecipeHolder<?>> recipes
    ) {
        return AssemblyRecipeLogic.createAssemblyRecipesFrom(recipes)
                .stream()
                .filter(recipe -> recipe.value().getDisplay().equals(display))
                .map(RecipeHolder::id)
                .findFirst();
    }

    public static boolean isMissingRequirements(
            List<? extends List<ItemStack>> requirements,
            AssemblyScreenHandler menu
    ) {
        return !RecipeTransferAvailability.canSatisfy(requirements, availableStacks(menu));
    }

    private static List<ItemStack> availableStacks(AssemblyScreenHandler menu) {
        List<ItemStack> stacks = new ArrayList<>();

        addSlots(
                stacks,
                menu,
                AssemblyScreenHandler.CRAFTING_SLOT_START_INDEX,
                AssemblyScreenHandler.CRAFTING_SLOT_COUNT
        );

        addSlots(
                stacks,
                menu,
                AssemblyScreenHandler.PATTERN_SLOT_INDEX,
                AssemblyScreenHandler.PATTERN_SLOT_COUNT
        );

        addSlots(
                stacks,
                menu,
                AssemblyScreenHandler.INVENTORY_SLOT_START_INDEX,
                AssemblyScreenHandler.INVENTORY_SLOT_COUNT
        );

        return stacks;
    }

    private static void addSlots(
            List<ItemStack> stacks,
            AssemblyScreenHandler menu,
            int start,
            int count
    ) {
        menu.slots
                .subList(start, start + count)
                .stream()
                .map(Slot::getItem)
                .map(ItemStack::copy)
                .forEach(stacks::add);
    }
}

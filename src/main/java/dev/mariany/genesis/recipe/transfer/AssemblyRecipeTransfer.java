package dev.mariany.genesis.recipe.transfer;

import dev.mariany.genesis.logic.AssemblyRecipeLogic;
import dev.mariany.genesis.recipe.AssemblyRecipe;
import dev.mariany.genesis.recipe.display.AssemblyCraftingRecipeDisplay;
import dev.mariany.genesis.screen.AssemblyScreenHandler;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.Arrays;
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
        for (RecipeHolder<AssemblyRecipe> recipe : AssemblyRecipeLogic.createAssemblyRecipesFrom(recipes)) {
            if (recipe.value().getDisplay().equals(display)) {
                return Optional.of(recipe.id());
            }
        }

        return Optional.empty();
    }

    public static boolean isMissingRequirements(
            List<? extends List<ItemStack>> requirements,
            AssemblyScreenHandler menu
    ) {
        return !canSatisfy(requirements, menu);
    }

    private static boolean canSatisfy(List<? extends List<ItemStack>> requirements, AssemblyScreenHandler menu) {
        List<List<ItemStack>> populatedRequirements = populatedRequirements(requirements);
        int requirementCount = populatedRequirements.size();

        if (requirementCount == 0) {
            return true;
        }

        List<Item> availableItems = collectAvailableItems(menu, requirementCount);

        if (availableItems.size() < requirementCount) {
            return false;
        }

        int[] matchedRequirements = new int[availableItems.size()];
        Arrays.fill(matchedRequirements, -1);

        for (int requirementIndex = 0; requirementIndex < requirementCount; requirementIndex++) {
            boolean[] visitedItems = new boolean[availableItems.size()];

            if (!assign(requirementIndex, populatedRequirements, availableItems, matchedRequirements, visitedItems)) {
                return false;
            }
        }

        return true;
    }

    private static List<List<ItemStack>> populatedRequirements(List<? extends List<ItemStack>> requirements) {
        List<List<ItemStack>> populated = new ArrayList<>(requirements.size());

        for (List<ItemStack> requirement : requirements) {
            if (!requirement.isEmpty()) {
                populated.add(requirement);
            }
        }

        return populated;
    }

    private static boolean assign(
            int requirementIndex,
            List<List<ItemStack>> requirements,
            List<Item> availableItems,
            int[] matchedRequirements,
            boolean[] visitedItems
    ) {
        List<ItemStack> requirement = requirements.get(requirementIndex);

        for (int itemIndex = 0; itemIndex < availableItems.size(); itemIndex++) {
            if (visitedItems[itemIndex] || !matchesAny(availableItems.get(itemIndex), requirement)) {
                continue;
            }

            visitedItems[itemIndex] = true;

            int matchedRequirement = matchedRequirements[itemIndex];

            if (matchedRequirement == -1 || assign(
                    matchedRequirement,
                    requirements,
                    availableItems,
                    matchedRequirements,
                    visitedItems
            )) {
                matchedRequirements[itemIndex] = requirementIndex;
                return true;
            }
        }

        return false;
    }

    private static boolean matchesAny(Item available, List<ItemStack> alternatives) {
        for (ItemStack required : alternatives) {
            if (available == required.getItem()) {
                return true;
            }
        }

        return false;
    }

    private static List<Item> collectAvailableItems(AssemblyScreenHandler menu, int countLimit) {
        int maximumSlots = AssemblyScreenHandler.CRAFTING_SLOT_COUNT
                + AssemblyScreenHandler.PATTERN_SLOT_COUNT
                + AssemblyScreenHandler.INVENTORY_SLOT_COUNT;

        List<Item> items = new ArrayList<>(maximumSlots);

        addSlots(
                items,
                menu,
                AssemblyScreenHandler.CRAFTING_SLOT_START_INDEX,
                AssemblyScreenHandler.CRAFTING_SLOT_COUNT,
                countLimit
        );

        addSlots(
                items,
                menu,
                AssemblyScreenHandler.PATTERN_SLOT_INDEX,
                AssemblyScreenHandler.PATTERN_SLOT_COUNT,
                countLimit
        );

        addSlots(
                items,
                menu,
                AssemblyScreenHandler.INVENTORY_SLOT_START_INDEX,
                AssemblyScreenHandler.INVENTORY_SLOT_COUNT,
                countLimit
        );

        return items;
    }

    private static void addSlots(List<Item> items, AssemblyScreenHandler menu, int start, int count, int countLimit) {
        int end = start + count;

        for (int slotIndex = start; slotIndex < end; slotIndex++) {
            ItemStack stack = menu.slots.get(slotIndex).getItem();

            for (int stackIndex = 0; stackIndex < Math.min(stack.getCount(), countLimit); stackIndex++) {
                items.add(stack.getItem());
            }
        }
    }
}

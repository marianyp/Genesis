package dev.mariany.genesis.recipe.transfer;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class RecipeTransferAvailability {
    private RecipeTransferAvailability() {
    }

    public static boolean canSatisfy(
            List<? extends List<ItemStack>> requirements,
            List<ItemStack> availableStacks
    ) {
        List<List<ItemStack>> populatedRequirements = requirements
                .stream()
                .filter(requirement -> !requirement.isEmpty())
                .map(List::copyOf)
                .sorted(Comparator.comparingInt(requirement -> matchingStackCount(
                        requirement,
                        availableStacks
                )))
                .toList();

        List<ItemStack> available = availableStacks
                .stream()
                .filter(stack -> !stack.isEmpty())
                .map(ItemStack::copy)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        return canSatisfy(populatedRequirements, available, 0);
    }

    private static boolean canSatisfy(
            List<List<ItemStack>> requirements,
            List<ItemStack> available,
            int requirementIndex
    ) {
        if (requirementIndex == requirements.size()) {
            return true;
        }

        List<ItemStack> alternatives = requirements.get(requirementIndex);

        for (ItemStack availableStack : available) {
            if (availableStack.isEmpty() || !matchesAny(availableStack, alternatives)) {
                continue;
            }

            availableStack.shrink(1);

            if (canSatisfy(requirements, available, requirementIndex + 1)) {
                return true;
            }

            availableStack.grow(1);
        }

        return false;
    }

    private static int matchingStackCount(List<ItemStack> requirement, List<ItemStack> available) {
        return available
                .stream()
                .filter(stack -> !stack.isEmpty() && matchesAny(stack, requirement))
                .mapToInt(ItemStack::getCount)
                .sum();
    }

    private static boolean matchesAny(ItemStack available, List<ItemStack> alternatives) {
        return alternatives.stream().anyMatch(alternative -> available.is(alternative.getItem()));
    }
}

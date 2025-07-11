package dev.mariany.genesis.client.age;

import dev.mariany.genesis.Genesis;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClientAgeManager {
    private static final ClientAgeManager INSTANCE = new ClientAgeManager();

    private final List<Ingredient> unlocks = new ArrayList<>();

    private ClientAgeManager() {
    }

    public static ClientAgeManager getInstance() {
        return INSTANCE;
    }

    public void updateLocked(Collection<Ingredient> collection) {
        int oldSize = unlocks.size();

        unlocks.clear();
        unlocks.addAll(collection);

        Genesis.LOGGER.info("Updated age unlocks. Old Size: {} | New Size: {}", oldSize, unlocks.size());
    }

    public boolean isUnlocked(ItemStack stack) {
        return unlocks.stream().noneMatch(ingredient -> ingredient.test(stack));
    }
}

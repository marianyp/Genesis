package dev.mariany.genesis.client.age;

import dev.mariany.genesis.Genesis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ClientAgeManager {
    private static final ClientAgeManager INSTANCE = new ClientAgeManager();

    private final List<Ingredient> itemUnlocks = new ArrayList<>();

    private ClientAgeManager() {
    }

    public static ClientAgeManager getInstance() {
        return INSTANCE;
    }

    public void updateItemUnlocks(Collection<Ingredient> collection) {
        int oldSize = itemUnlocks.size();

        itemUnlocks.clear();
        itemUnlocks.addAll(collection);

        Genesis.LOGGER.info("Updated age items. Old Size: {} | New Size: {}", oldSize, itemUnlocks.size());
    }

    public boolean isUnlocked(ItemStack stack) {
        return itemUnlocks.stream().noneMatch(ingredient -> ingredient.test(stack));
    }
}

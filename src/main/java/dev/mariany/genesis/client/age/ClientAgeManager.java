package dev.mariany.genesis.client.age;

import dev.mariany.genesis.Genesis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.RecipeToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;

import java.util.*;

@Environment(EnvType.CLIENT)
public class ClientAgeManager {
    private static final ClientAgeManager INSTANCE = new ClientAgeManager();

    private final List<Ingredient> itemUnlocks = new ArrayList<>();
    private boolean initiatedItemUnlocks = false;

    private ClientAgeManager() {
    }

    public static ClientAgeManager getInstance() {
        return INSTANCE;
    }

    public void reset() {
        Genesis.LOGGER.info("Resetting Client Age Manager");

        itemUnlocks.clear();
        initiatedItemUnlocks = false;
    }

    public boolean isUnlocked(ItemStack stack) {
        return itemUnlocks.stream().noneMatch(ingredient -> ingredient.test(stack));
    }

    public void updateItemUnlocks(Collection<Ingredient> changes) {
        boolean initial = !initiatedItemUnlocks;
        int oldSize = itemUnlocks.size();
        List<Ingredient> difference = getDifference(itemUnlocks, changes);

        itemUnlocks.clear();
        itemUnlocks.addAll(changes);
        initiatedItemUnlocks = true;

        Genesis.LOGGER.info("Updated age instructions. Old Size: {} | New Size: {}", oldSize, itemUnlocks.size());

        if (!initial) {
            afterUpdateItemUnlocks(difference);
        }
    }

    private static List<Ingredient> getDifference(
            Collection<Ingredient> before,
            Collection<Ingredient> after
    ) {
        Set<Ingredient> ingredients = new HashSet<>(after);
        return before.stream()
                .filter(oldIngredient -> !ingredients.contains(oldIngredient))
                .toList();
    }

    private void afterUpdateItemUnlocks(Collection<Ingredient> changes) {
        MinecraftClient client = MinecraftClient.getInstance();
        ToastManager toastManager = client.getToastManager();

        for (Ingredient ingredient : changes) {
            ingredient.getMatchingItems().forEach(entry -> RecipeToast.show(toastManager,
                            new RecipeDisplay() {
                                @Override
                                public SlotDisplay result() {
                                    return new SlotDisplay.StackSlotDisplay(entry.value().getDefaultStack());
                                }

                                @Override
                                public SlotDisplay craftingStation() {
                                    return new SlotDisplay.StackSlotDisplay(Items.AIR.getDefaultStack());
                                }

                                @Override
                                public Serializer<? extends RecipeDisplay> serializer() {
                                    return null;
                                }
                            }
                    )
            );
        }
    }
}

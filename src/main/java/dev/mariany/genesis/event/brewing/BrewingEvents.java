package dev.mariany.genesis.event.brewing;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.jetbrains.annotations.Nullable;

public final class BrewingEvents {
    public static final Event<AllowIngredient> ALLOW_INGREDIENT = EventFactory.createArrayBacked(
            AllowIngredient.class,
            callbacks -> item -> {
                for (AllowIngredient callback : callbacks) {
                    if (callback.allow(item)) {
                        return true;
                    }
                }

                return false;
            }
    );

    public static final Event<AllowInput> ALLOW_INPUT = EventFactory.createArrayBacked(
            AllowInput.class,
            callbacks -> (brewingStand, slot, stack) -> {
                for (AllowInput callback : callbacks) {
                    if (callback.allow(brewingStand, slot, stack)) {
                        return true;
                    }
                }

                return false;
            }
    );

    public static final Event<Mix> MIX = EventFactory.createArrayBacked(
            Mix.class,
            callbacks -> (ingredient, input) -> {
                for (Mix callback : callbacks) {
                    ItemStack result = callback.mix(ingredient, input);

                    if (result != null) {
                        return result;
                    }
                }

                return null;
            }
    );

    private BrewingEvents() {
    }

    @FunctionalInterface
    public interface AllowIngredient {
        boolean allow(Item item);
    }

    @FunctionalInterface
    public interface AllowInput {
        boolean allow(BrewingStandBlockEntity brewingStand, int slot, ItemStack stack);
    }

    @FunctionalInterface
    public interface Mix {
        @Nullable
        ItemStack mix(ItemStack ingredient, ItemStack input);
    }
}

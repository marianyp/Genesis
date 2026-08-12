package dev.mariany.genesis.client.recipe.display;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.recipe.display.RecipeDisplayRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.recipe.v1.sync.ClientRecipeSynchronizedEvent;
import net.fabricmc.fabric.api.recipe.v1.sync.SynchronizedRecipes;
import net.minecraft.client.Minecraft;

import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.*;

@Environment(EnvType.CLIENT)
public class ClientRecipeDisplayRegistry extends RecipeDisplayRegistry {
    private final Set<Runnable> refreshListeners = new HashSet<>();
    private Collection<RecipeHolder<?>> recipes = List.of();

    public void bootstrap() {
        ClientRecipeSynchronizedEvent.EVENT.register(this::onClientRecipeSynchronized);
        Genesis.PRIMITIVE_CAULDRON_LOOT.addUpdateListener(this::onPrimitiveCauldronLootUpdated);
    }

    private void onPrimitiveCauldronLootUpdated() {
        Minecraft.getInstance().execute(this::refreshCached);
    }

    private void onClientRecipeSynchronized(Minecraft minecraft, SynchronizedRecipes synchronizedRecipes) {
        this.recipes = List.copyOf(synchronizedRecipes.recipes());
        this.refreshCached();
    }

    public void refreshCached() {
        this.refresh(this.recipes);
        List.copyOf(this.refreshListeners).forEach(Runnable::run);
    }

    public void addRefreshListener(Runnable listener) {
        this.refreshListeners.add(listener);
    }

    public void removeRefreshListener(Runnable listener) {
        this.refreshListeners.remove(listener);
    }

    public List<RecipeHolder<?>> recipes() {
        return List.copyOf(this.recipes);
    }
}

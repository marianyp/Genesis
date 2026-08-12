package dev.mariany.genesis.recipe.display;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.RecipeDisplay;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class RecipeDisplayTransfer<D extends RecipeDisplay, M extends AbstractContainerMenu> {
    private final Class<M> menuClass;
    private final Supplier<MenuType<M>> menuType;
    private final BiFunction<D, Collection<RecipeHolder<?>>, Optional<ResourceKey<Recipe<?>>>> recipeKeyResolver;
    private final RequirementChecker<M> requirementChecker;
    private final Function<ResourceKey<Recipe<?>>, CustomPacketPayload> payloadFactory;

    public RecipeDisplayTransfer(
            Class<M> menuClass,
            Supplier<MenuType<M>> menuType,
            BiFunction<D, Collection<RecipeHolder<?>>, Optional<ResourceKey<Recipe<?>>>> recipeKeyResolver,
            RequirementChecker<M> requirementChecker,
            Function<ResourceKey<Recipe<?>>, CustomPacketPayload> payloadFactory
    ) {
        this.menuClass = Objects.requireNonNull(menuClass, "menuClass");
        this.menuType = Objects.requireNonNull(menuType, "menuType");
        this.recipeKeyResolver = Objects.requireNonNull(recipeKeyResolver, "recipeKeyResolver");
        this.requirementChecker = Objects.requireNonNull(requirementChecker, "requirementChecker");
        this.payloadFactory = Objects.requireNonNull(payloadFactory, "payloadFactory");
    }

    public Class<M> menuClass() {
        return this.menuClass;
    }

    public MenuType<M> menuType() {
        return this.menuType.get();
    }

    public Optional<ResourceKey<Recipe<?>>> findRecipeKey(D display, Collection<RecipeHolder<?>> recipes) {
        return this.recipeKeyResolver.apply(display, recipes);
    }

    public boolean isMissingRequirements(List<? extends List<ItemStack>> requirements, M menu) {
        return this.requirementChecker.isMissing(requirements, menu);
    }

    public CustomPacketPayload createPayload(ResourceKey<Recipe<?>> recipeKey) {
        return this.payloadFactory.apply(recipeKey);
    }

    public Component missingIngredientsMessage() {
        return Component.translatable("recipe_transfer.genesis.missing_ingredients");
    }

    public Component unknownRecipeMessage() {
        return Component.translatable("recipe_transfer.genesis.unknown_recipe");
    }

    @FunctionalInterface
    public interface RequirementChecker<M extends AbstractContainerMenu> {
        boolean isMissing(List<? extends List<ItemStack>> requirements, M menu);
    }
}

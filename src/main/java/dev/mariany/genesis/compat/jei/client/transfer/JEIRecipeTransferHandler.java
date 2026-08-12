package dev.mariany.genesis.compat.jei.client.transfer;

import dev.mariany.genesis.client.GenesisClient;
import dev.mariany.genesis.recipe.display.RecipeDisplayTransfer;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.types.IRecipeType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public final class JEIRecipeTransferHandler<
        D extends RecipeDisplay,
        M extends AbstractContainerMenu
        > implements IRecipeTransferHandler<M, D> {
    private final IRecipeType<D> recipeType;
    private final RecipeDisplayTransfer<D, M> transfer;
    private final IRecipeTransferHandlerHelper transferHelper;

    public JEIRecipeTransferHandler(
            IRecipeType<D> recipeType,
            RecipeDisplayTransfer<D, M> transfer,
            IRecipeTransferHandlerHelper transferHelper
    ) {
        this.recipeType = recipeType;
        this.transfer = transfer;
        this.transferHelper = transferHelper;
    }

    @Override
    public Class<? extends M> getContainerClass() {
        return this.transfer.menuClass();
    }

    @Override
    public Optional<MenuType<M>> getMenuType() {
        return Optional.of(this.transfer.menuType());
    }

    @Override
    public IRecipeType<D> getRecipeType() {
        return this.recipeType;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(
            M container,
            D recipe,
            IRecipeSlotsView recipeSlots,
            Player player,
            boolean maxTransfer,
            boolean doTransfer
    ) {
        Optional<ResourceKey<Recipe<?>>> recipeKey = this.transfer.findRecipeKey(
                recipe,
                GenesisClient.RECIPE_DISPLAY_REGISTRY.recipes()
        );

        if (recipeKey.isEmpty()) {
            return this.transferHelper.createUserErrorWithTooltip(this.transfer.unknownRecipeMessage());
        }

        List<List<ItemStack>> requirements = recipeSlots
                .getSlotViews(RecipeIngredientRole.INPUT)
                .stream()
                .filter(slot -> !slot.isEmpty())
                .map(IRecipeSlotView::getItemStacks)
                .map(stream -> stream.map(ItemStack::copy).toList())
                .toList();

        if (this.transfer.isMissingRequirements(requirements, container)) {
            return this.transferHelper.createUserErrorWithTooltip(this.transfer.missingIngredientsMessage());
        }

        if (doTransfer) {
            ClientPlayNetworking.send(this.transfer.createPayload(recipeKey.get()));
        }

        return null;
    }
}

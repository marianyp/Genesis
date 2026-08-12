package dev.mariany.genesis.compat.rei.client;

import dev.mariany.genesis.compat.rei.display.adapter.REIRecipeDisplayAdapter;
import dev.mariany.genesis.recipe.display.RecipeDisplayContents;
import dev.mariany.genesis.recipe.display.RecipeDisplayTransfer;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandler;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.plugin.common.displays.crafting.CraftingDisplay;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public final class REIRecipeTransferHandler<
        D extends CraftingDisplay,
        M extends AbstractContainerMenu
        > implements TransferHandler {
    private final REIRecipeDisplayAdapter<D> adapter;
    private final RecipeDisplayTransfer<?, M> transfer;

    public REIRecipeTransferHandler(
            REIRecipeDisplayAdapter<D> adapter,
            RecipeDisplayTransfer<?, M> transfer
    ) {
        this.adapter = adapter;
        this.transfer = transfer;
    }

    @Override
    public ApplicabilityResult checkApplicable(Context context) {
        return context.getMenu() != null
                && this.transfer.menuClass().isInstance(context.getMenu())
                && this.adapter.supports(context.getDisplay())
                ? ApplicabilityResult.createApplicable()
                : ApplicabilityResult.createNotApplicable();
    }

    @Override
    public Result handle(Context context) {
        D display = this.adapter.cast(context.getDisplay());
        M menu = this.transfer.menuClass().cast(context.getMenu());
        Optional<Identifier> recipeLocation = display.getDisplayLocation();

        if (recipeLocation.isEmpty()) {
            return Result.createFailed(this.transfer.unknownRecipeMessage()).blocksFurtherHandling(false);
        }

        RecipeDisplayContents<EntryIngredient> contents = this.adapter.contents(display);

        List<List<ItemStack>> requirements = Stream
                .concat(contents.additionalInputs().stream(), contents.craftingInputs().stream())
                .map(REIRecipeTransferHandler::itemStacks)
                .filter(ingredient -> !ingredient.isEmpty())
                .toList();

        if (this.transfer.isMissingRequirements(requirements, menu)) {
            return Result.createFailed(this.transfer.missingIngredientsMessage())
                         .blocksFurtherHandling(false);
        }

        if (context.isActuallyCrafting()) {
            ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, recipeLocation.get());
            ClientPlayNetworking.send(this.transfer.createPayload(recipeKey));
        }

        return Result.createSuccessful().blocksFurtherHandling();
    }

    private static List<ItemStack> itemStacks(EntryIngredient ingredient) {
        return ingredient
                .stream()
                .filter(entry -> entry.getType() == VanillaEntryTypes.ITEM)
                .map(entry -> ((ItemStack) entry.getValue()).copy())
                .toList();
    }
}

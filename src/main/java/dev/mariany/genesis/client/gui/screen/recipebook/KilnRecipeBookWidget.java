package dev.mariany.genesis.client.gui.screen.recipebook;

import dev.mariany.genesis.mixin.accessor.GhostRecipeAccessor;
import dev.mariany.genesis.screen.KilnScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import java.util.List;

@Environment(EnvType.CLIENT)
public class KilnRecipeBookWidget extends RecipeBookComponent<KilnScreenHandler> {
    private static final WidgetSprites TEXTURES = new WidgetSprites(
            Identifier.withDefaultNamespace("recipe_book/furnace_filter_enabled"),
            Identifier.withDefaultNamespace("recipe_book/furnace_filter_disabled"),
            Identifier.withDefaultNamespace("recipe_book/furnace_filter_enabled_highlighted"),
            Identifier.withDefaultNamespace("recipe_book/furnace_filter_disabled_highlighted")
    );
    private final Component toggleCraftableButtonText;

    public KilnRecipeBookWidget(KilnScreenHandler screenHandler, Component toggleCraftableButtonText, List<TabInfo> tabs) {
        super(screenHandler, tabs);
        this.toggleCraftableButtonText = toggleCraftableButtonText;
    }

    @Override
    protected WidgetSprites getFilterButtonTextures() {
        return TEXTURES;
    }

    @Override
    protected boolean isCraftingSlot(Slot slot) {
        return switch (slot.index) {
            case 0, 1, 2 -> true;
            default -> false;
        };
    }

    @Override
    protected void fillGhostRecipe(GhostSlots _ghostRecipe, RecipeDisplay display, ContextMap context) {
        GhostRecipeAccessor ghostRecipe = ((GhostRecipeAccessor) _ghostRecipe);
        ghostRecipe.genesis$addResults(this.menu.getOutputSlot(), context, display.result());

        if (!(display instanceof FurnaceRecipeDisplay furnaceRecipeDisplay)) {
            return;
        }

        ghostRecipe.genesis$addInputs(this.menu.slots.get(0), context, furnaceRecipeDisplay.ingredient());

        Slot slot = this.menu.slots.get(1);

        if (!slot.getItem().isEmpty()) {
            return;
        }

        ghostRecipe.genesis$addInputs(slot, context, furnaceRecipeDisplay.fuel());
    }

    @Override
    protected Component getRecipeFilterName() {
        return this.toggleCraftableButtonText;
    }

    @Override
    protected void selectMatchingRecipes(RecipeCollection recipeResultCollection, StackedItemContents recipeFinder) {
        recipeResultCollection.selectRecipes(recipeFinder, display -> display instanceof FurnaceRecipeDisplay);
    }
}

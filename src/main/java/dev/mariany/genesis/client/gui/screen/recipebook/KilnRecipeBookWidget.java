package dev.mariany.genesis.client.gui.screen.recipebook;

import dev.mariany.genesis.mixin.accessor.GhostRecipeAccessor;
import dev.mariany.genesis.screen.KilnScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.recipebook.GhostRecipe;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.display.FurnaceRecipeDisplay;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameterMap;

import java.util.List;

@Environment(EnvType.CLIENT)
public class KilnRecipeBookWidget extends RecipeBookWidget<KilnScreenHandler> {
    private static final ButtonTextures TEXTURES = new ButtonTextures(
            Identifier.ofVanilla("recipe_book/furnace_filter_enabled"),
            Identifier.ofVanilla("recipe_book/furnace_filter_disabled"),
            Identifier.ofVanilla("recipe_book/furnace_filter_enabled_highlighted"),
            Identifier.ofVanilla("recipe_book/furnace_filter_disabled_highlighted")
    );
    private final Text toggleCraftableButtonText;

    public KilnRecipeBookWidget(KilnScreenHandler screenHandler, Text toggleCraftableButtonText, List<Tab> tabs) {
        super(screenHandler, tabs);
        this.toggleCraftableButtonText = toggleCraftableButtonText;
    }

    @Override
    protected void setBookButtonTexture() {
        this.toggleCraftableButton.setTextures(TEXTURES);
    }

    @Override
    protected boolean isValid(Slot slot) {
        return switch (slot.id) {
            case 0, 1, 2 -> true;
            default -> false;
        };
    }

    @Override
    protected void showGhostRecipe(GhostRecipe _ghostRecipe, RecipeDisplay display, ContextParameterMap context) {
        GhostRecipeAccessor ghostRecipe = ((GhostRecipeAccessor) _ghostRecipe);
        ghostRecipe.genesis$addResults(this.craftingScreenHandler.getOutputSlot(), context, display.result());

        if (display instanceof FurnaceRecipeDisplay furnaceRecipeDisplay) {
            ghostRecipe.genesis$addInputs(this.craftingScreenHandler.slots.get(0), context, furnaceRecipeDisplay.ingredient());
            Slot slot = this.craftingScreenHandler.slots.get(1);

            if (slot.getStack().isEmpty()) {
                ghostRecipe.genesis$addInputs(slot, context, furnaceRecipeDisplay.fuel());
            }
        }
    }

    @Override
    protected Text getToggleCraftableButtonText() {
        return this.toggleCraftableButtonText;
    }

    @Override
    protected void populateRecipes(RecipeResultCollection recipeResultCollection, RecipeFinder recipeFinder) {
        recipeResultCollection.populateRecipes(recipeFinder, display -> display instanceof FurnaceRecipeDisplay);
    }
}
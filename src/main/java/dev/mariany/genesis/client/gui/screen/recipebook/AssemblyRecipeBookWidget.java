package dev.mariany.genesis.client.gui.screen.recipebook;

import dev.mariany.genesis.item.custom.AssemblyPatternItem;
import dev.mariany.genesis.mixin.accessor.GhostRecipeAccessor;
import dev.mariany.genesis.recipe.display.AssemblyCraftingRecipeDisplay;
import dev.mariany.genesis.screen.AssemblyScreenHandler;
import dev.mariany.genesis.screen.slot.AssemblyInputSlot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.recipebook.AbstractCraftingRecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.GhostRecipe;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeGridAligner;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.context.ContextParameterMap;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class AssemblyRecipeBookWidget extends AbstractCraftingRecipeBookWidget {
    public AssemblyRecipeBookWidget(AssemblyScreenHandler screenHandler) {
        super(screenHandler);
    }

    public void close() {
        this.setOpen(false);
    }

    @Override
    protected void showGhostRecipe(GhostRecipe ghostRecipe, RecipeDisplay display, ContextParameterMap context) {
        super.showGhostRecipe(ghostRecipe, display, context);

        if (display instanceof AssemblyCraftingRecipeDisplay assemblyCraftingRecipeDisplay) {
            List<Slot> list = this.craftingScreenHandler.getInputSlots();

            RecipeGridAligner.alignRecipeToGrid(
                    this.craftingScreenHandler.getWidth(),
                    this.craftingScreenHandler.getHeight(),
                    assemblyCraftingRecipeDisplay.width(),
                    assemblyCraftingRecipeDisplay.height(),
                    assemblyCraftingRecipeDisplay.ingredients(),
                    (slot, index, x, y) -> {
                        if (list.get(index) instanceof AssemblyInputSlot assemblyInputSlot) {
                            if (assemblyInputSlot.canInsert()) {
                                ((GhostRecipeAccessor) ghostRecipe).genesis$addInputs(assemblyInputSlot, context, slot);
                            }
                        }
                    }
            );
        }
    }

    @Override
    protected void populateRecipes(RecipeResultCollection recipeResultCollection, RecipeFinder recipeFinder) {
        recipeResultCollection.populateRecipes(recipeFinder, this::canDisplay);
    }

    private boolean canDisplay(RecipeDisplay display) {
        if (this.craftingScreenHandler instanceof AssemblyScreenHandler assemblyScreenHandler) {
            ContextParameterMap context = SlotDisplayContexts.createParameters(
                    Objects.requireNonNull(this.client.world)
            );

            List<ItemStack> stacks = display.result().getStacks(context);

            Optional<AssemblyPatternItem> optionalAssemblyPatternItem = assemblyScreenHandler.getAssemblyPatternItem();

            if (optionalAssemblyPatternItem.isPresent()) {
                AssemblyPatternItem assemblyPatternItem = optionalAssemblyPatternItem.get();

                for (ItemStack stack : stacks) {
                    if (!stack.isIn(assemblyPatternItem.getCrafts())) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }

        int width = this.craftingScreenHandler.getWidth();
        int height = this.craftingScreenHandler.getHeight();

        if (display instanceof AssemblyCraftingRecipeDisplay assemblyCraftingRecipeDisplay) {
            return width >= assemblyCraftingRecipeDisplay.width() && height >= assemblyCraftingRecipeDisplay.height();
        }

        return false;
    }
}

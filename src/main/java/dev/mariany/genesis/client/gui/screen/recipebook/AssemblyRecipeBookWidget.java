package dev.mariany.genesis.client.gui.screen.recipebook;

import dev.mariany.genesis.item.custom.AssemblyPatternItem;
import dev.mariany.genesis.mixin.accessor.GhostRecipeAccessor;
import dev.mariany.genesis.recipe.display.AssemblyCraftingRecipeDisplay;
import dev.mariany.genesis.screen.AssemblyScreenHandler;
import dev.mariany.genesis.screen.slot.AssemblyInputSlot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.recipebook.CraftingRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.GhostSlots;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.recipebook.PlaceRecipeHelper;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class AssemblyRecipeBookWidget extends CraftingRecipeBookComponent {
    public AssemblyRecipeBookWidget(AssemblyScreenHandler screenHandler) {
        super(screenHandler);
    }

    public void close() {
        this.setVisible(false);
    }

    @Override
    protected void fillGhostRecipe(GhostSlots ghostRecipe, RecipeDisplay display, ContextMap context) {
        super.fillGhostRecipe(ghostRecipe, display, context);

        if (display instanceof AssemblyCraftingRecipeDisplay assemblyCraftingRecipeDisplay) {
            List<Slot> list = this.menu.getInputGridSlots();

            PlaceRecipeHelper.placeRecipe(
                    this.menu.getGridWidth(),
                    this.menu.getGridHeight(),
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
    protected void selectMatchingRecipes(RecipeCollection recipeResultCollection, StackedItemContents recipeFinder) {
        recipeResultCollection.selectRecipes(recipeFinder, this::canDisplay);
    }

    private boolean canDisplay(RecipeDisplay display) {
        if (this.menu instanceof AssemblyScreenHandler assemblyScreenHandler) {
            ContextMap context = SlotDisplayContext.fromLevel(
                    Objects.requireNonNull(this.minecraft.level)
            );

            List<ItemStack> stacks = display.result().resolveForStacks(context);

            Optional<AssemblyPatternItem> optionalAssemblyPatternItem = assemblyScreenHandler.getAssemblyPatternItem();

            if (optionalAssemblyPatternItem.isPresent()) {
                AssemblyPatternItem assemblyPatternItem = optionalAssemblyPatternItem.get();

                for (ItemStack stack : stacks) {
                    if (!stack.is(assemblyPatternItem.getCrafts())) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }

        int width = this.menu.getGridWidth();
        int height = this.menu.getGridHeight();

        if (display instanceof AssemblyCraftingRecipeDisplay assemblyCraftingRecipeDisplay) {
            return width >= assemblyCraftingRecipeDisplay.width() && height >= assemblyCraftingRecipeDisplay.height();
        }

        return false;
    }
}

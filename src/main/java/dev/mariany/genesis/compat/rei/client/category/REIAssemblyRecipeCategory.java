package dev.mariany.genesis.compat.rei.client.category;

import dev.mariany.genesis.client.gui.AssemblySlotTextures;
import dev.mariany.genesis.compat.rei.display.REIAssemblyDisplay;
import dev.mariany.genesis.compat.rei.display.adapter.REIRecipeDisplayAdapter;
import dev.mariany.genesis.item.custom.AssemblyPatternItem;
import dev.mariany.genesis.recipe.CraftingPattern;
import dev.mariany.genesis.recipe.display.RecipeDisplayLayout;
import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class REIAssemblyRecipeCategory extends REIRecipeCategory<REIAssemblyDisplay> {
    public REIAssemblyRecipeCategory(REIRecipeDisplayAdapter<REIAssemblyDisplay> adapter) {
        super(adapter);
    }

    @Override
    protected void setupRecipe(
            REIAssemblyDisplay display,
            Point layoutOrigin,
            List<RecipeDisplayLayout.PopulatedSlot<EntryIngredient>> populatedSlots,
            List<Widget> widgets
    ) {
        List<Widget> slotWidgets = new ArrayList<>();
        List<AssemblySlotWidget> assemblySlotWidgets = new ArrayList<>();
        Slot patternSlot = null;

        for (RecipeDisplayLayout.PopulatedSlot<EntryIngredient> populatedSlot : populatedSlots) {
            Slot slot = this.addSlot(slotWidgets, layoutOrigin, populatedSlot);
            RecipeDisplayLayout.Slot layoutSlot = populatedSlot.slot();

            if (layoutSlot.type() == RecipeDisplayLayout.SlotType.ADDITIONAL_INPUT &&
                    layoutSlot.index() == 0) {
                patternSlot = slot;
            } else if (layoutSlot.isCraftingInput()) {
                slot.disableBackground();
                assemblySlotWidgets.add(new AssemblySlotWidget(layoutSlot, slot));
            }
        }

        if (patternSlot != null) {
            widgets.add(createAssemblySlotBackgrounds(layoutOrigin, assemblySlotWidgets, patternSlot));
        }

        widgets.addAll(slotWidgets);
    }

    private static Widget createAssemblySlotBackgrounds(
            Point layoutOrigin,
            List<AssemblySlotWidget> assemblySlotWidgets,
            Slot patternSlot
    ) {
        CraftingPattern initialPattern = craftingPattern(patternSlot.getCurrentEntry());

        assemblySlotWidgets.forEach(slot -> slot.setLocked(
                initialPattern.isSlotDisabled(slot.layoutSlot().index())
        ));

        return Widgets.createDrawableWidget((graphics, _, _, _) -> {
            CraftingPattern pattern = craftingPattern(patternSlot.getCurrentEntry());

            assemblySlotWidgets.forEach(assemblySlotWidget -> {
                RecipeDisplayLayout.Slot layoutSlot = assemblySlotWidget.layoutSlot();
                RecipeDisplayLayout.Position position = layoutSlot.position();
                boolean locked = pattern.isSlotDisabled(layoutSlot.index());

                assemblySlotWidget.setLocked(locked);

                AssemblySlotTextures.draw(
                        graphics,
                        layoutSlot.index(),
                        locked,
                        layoutOrigin.x + position.x(),
                        layoutOrigin.y + position.y()
                );
            });
        });
    }

    private static CraftingPattern craftingPattern(EntryStack<?> entry) {
        Object value = entry.getValue();

        if (value instanceof ItemStack stack && stack.getItem() instanceof AssemblyPatternItem patternItem) {
            return patternItem.getCraftingPattern();
        }

        return CraftingPattern.ALL;
    }

    private record AssemblySlotWidget(RecipeDisplayLayout.Slot layoutSlot, Slot slot) {
        private void setLocked(boolean locked) {
            this.slot.setInteractable(!locked);
            this.slot.setInteractableFavorites(!locked);
            this.slot.setHighlightEnabled(!locked);
            this.slot.setTooltipsEnabled(!locked);
        }
    }
}

package dev.mariany.genesis.compat.jei.client.category;

import dev.mariany.genesis.client.gui.AssemblySlotTextures;
import dev.mariany.genesis.item.custom.AssemblyPatternItem;
import dev.mariany.genesis.recipe.CraftingPattern;
import dev.mariany.genesis.recipe.display.AssemblyCraftingRecipeDisplay;
import dev.mariany.genesis.recipe.display.RecipeDisplayLayout;
import dev.mariany.genesis.recipe.display.type.RecipeDisplayType;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.inputs.RecipeSlotUnderMouse;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.ISlottedRecipeWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class JEIAssemblyRecipeCategory extends JEIRecipeCategory<AssemblyCraftingRecipeDisplay> {
    public JEIAssemblyRecipeCategory(
            IGuiHelper guiHelper,
            IRecipeType<AssemblyCraftingRecipeDisplay> recipeType,
            RecipeDisplayType<AssemblyCraftingRecipeDisplay> displayType
    ) {
        super(guiHelper, recipeType, displayType);
    }

    @Override
    protected boolean hasStandardSlotBackground(
            AssemblyCraftingRecipeDisplay display,
            RecipeDisplayLayout.Slot slot
    ) {
        return !slot.isCraftingInput();
    }

    @Override
    public void createRecipeExtras(
            IRecipeExtrasBuilder builder,
            AssemblyCraftingRecipeDisplay display,
            IFocusGroup focuses
    ) {
        List<RecipeDisplayLayout.PopulatedSlot<SlotDisplay>> populatedSlots = this
                .displayType()
                .category()
                .layout()
                .populate(display.width(), display.contents());

        List<IRecipeSlotDrawable> recipeSlots = builder.getRecipeSlots().getSlots();

        if (populatedSlots.size() != recipeSlots.size()) {
            return;
        }

        IRecipeSlotDrawable patternSlot = null;
        List<AssemblyCraftingSlot> craftingSlots = new ArrayList<>();

        for (int i = 0; i < populatedSlots.size(); i++) {
            RecipeDisplayLayout.Slot slot = populatedSlots.get(i).slot();
            IRecipeSlotDrawable drawable = recipeSlots.get(i);

            if (slot.type() == RecipeDisplayLayout.SlotType.ADDITIONAL_INPUT && slot.index() == 0) {
                patternSlot = drawable;
            } else if (slot.isCraftingInput()) {
                craftingSlots.add(new AssemblyCraftingSlot(
                        slot.index(),
                        this.viewport().translate(slot.position()),
                        drawable
                ));
            }
        }

        if (patternSlot == null) {
            return;
        }

        builder.addSlottedWidget(
                new AssemblyCraftingSlotsWidget(craftingSlots, patternSlot),
                craftingSlots.stream().map(AssemblyCraftingSlot::drawable).toList()
        );
    }

    private static CraftingPattern craftingPattern(IRecipeSlotView patternSlot) {
        return patternSlot
                .getDisplayedItemStack()
                .map(ItemStack::getItem)
                .filter(AssemblyPatternItem.class::isInstance)
                .map(AssemblyPatternItem.class::cast)
                .map(AssemblyPatternItem::getCraftingPattern)
                .orElse(CraftingPattern.ALL);
    }

    private record AssemblyCraftingSlot(
            int index,
            RecipeDisplayLayout.Position position,
            IRecipeSlotDrawable drawable
    ) {
    }

    private record AssemblyCraftingSlotsWidget(
            List<AssemblyCraftingSlot> craftingSlots,
            IRecipeSlotDrawable patternSlot
    ) implements ISlottedRecipeWidget {
        private static final ScreenPosition POSITION = new ScreenPosition(0, 0);

        @Override
        public ScreenPosition getPosition() {
            return POSITION;
        }

        @Override
        public void drawWidget(GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
            IRecipeSlotDrawable hoveredSlot = this
                    .getSlotUnderMouse(mouseX, mouseY)
                    .map(RecipeSlotUnderMouse::slot)
                    .orElse(null);

            CraftingPattern pattern = craftingPattern(this.patternSlot);

            this.craftingSlots.forEach(slot -> {
                boolean locked = pattern.isSlotDisabled(slot.index());

                AssemblySlotTextures.draw(
                        graphics,
                        slot.index(),
                        locked,
                        slot.position().x(),
                        slot.position().y()
                );

                slot.drawable().draw(graphics, !locked && slot.drawable() == hoveredSlot);
            });
        }

        @Override
        public Optional<RecipeSlotUnderMouse> getSlotUnderMouse(double mouseX, double mouseY) {
            CraftingPattern pattern = craftingPattern(this.patternSlot);

            return this.craftingSlots
                    .stream()
                    .filter(slot -> !pattern.isSlotDisabled(slot.index()))
                    .map(AssemblyCraftingSlot::drawable)
                    .filter(slot -> slot.isMouseOver(mouseX, mouseY))
                    .findFirst()
                    .map(slot -> new RecipeSlotUnderMouse(slot, POSITION));
        }
    }
}

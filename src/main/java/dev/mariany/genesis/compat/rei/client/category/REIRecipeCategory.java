package dev.mariany.genesis.compat.rei.client.category;

import dev.mariany.genesis.compat.rei.display.adapter.REIRecipeDisplayAdapter;
import dev.mariany.genesis.recipe.display.RecipeDisplayCategory;
import dev.mariany.genesis.recipe.display.RecipeDisplayLayout;
import dev.mariany.genesis.recipe.display.RecipeDisplayViewport;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Arrow;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.client.categories.crafting.DefaultCraftingCategory;
import me.shedaniel.rei.plugin.common.displays.crafting.CraftingDisplay;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class REIRecipeCategory<D extends CraftingDisplay> extends DefaultCraftingCategory {
    private final REIRecipeDisplayAdapter<D> adapter;
    private final RecipeDisplayCategory category;
    private final RecipeDisplayViewport viewport;

    public REIRecipeCategory(REIRecipeDisplayAdapter<D> adapter) {
        this.adapter = adapter;
        this.category = adapter.displayType().category();
        this.viewport = RecipeDisplayViewport.standard(this.category);
    }

    @Override
    public CategoryIdentifier<? extends CraftingDisplay> getCategoryIdentifier() {
        return this.adapter.categoryIdentifier();
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(this.category.icon().get());
    }

    @Override
    public Component getTitle() {
        return Component.translatable(this.category.titleTranslationKey());
    }

    @Override
    public int getDisplayWidth(CraftingDisplay craftingDisplay) {
        return this.viewport.width();
    }

    @Override
    public int getDisplayHeight() {
        return this.viewport.height();
    }

    @Override
    public List<Widget> setupDisplay(CraftingDisplay craftingDisplay, Rectangle bounds) {
        RecipeDisplayLayout layout = this.category.layout();
        Point layoutOrigin = this.getLayoutOrigin(bounds);

        List<Widget> widgets = new ArrayList<>();

        widgets.add(Widgets.createRecipeBase(bounds));

        this.drawArrow(layoutOrigin, widgets::add);

        if (this.adapter.supports(craftingDisplay)) {
            D display = this.adapter.cast(craftingDisplay);

            List<RecipeDisplayLayout.PopulatedSlot<EntryIngredient>> populatedSlots = layout.populate(
                    this.adapter.recipeWidth(display),
                    this.adapter.contents(display)
            );

            this.setupRecipe(display, layoutOrigin, populatedSlots, widgets);
        }

        return widgets;
    }

    private Point getLayoutOrigin(Rectangle bounds) {
        RecipeDisplayLayout.Position offset = this.viewport.layoutOffset();

        int x = bounds.x + offset.x();
        int y = bounds.y + offset.y();

        return new Point(x, y);
    }

    private void drawArrow(Point layoutOrigin, Consumer<Widget> widgetConsumer) {
        RecipeDisplayLayout layout = this.category.layout();
        RecipeDisplayLayout.Position arrowPosition = layout.arrowPosition();

        if (arrowPosition == null) {
            return;
        }

        int x = layoutOrigin.x + arrowPosition.x();
        int y = layoutOrigin.y + arrowPosition.y();

        Arrow arrow = Widgets.createArrow(new Point(x, y));

        widgetConsumer.accept(arrow);
    }

    protected void setupRecipe(
            D display,
            Point layoutOrigin,
            List<RecipeDisplayLayout.PopulatedSlot<EntryIngredient>> populatedSlots,
            List<Widget> widgets
    ) {
        populatedSlots.forEach(populatedSlot -> this.addSlot(widgets, layoutOrigin, populatedSlot));
    }

    protected Slot addSlot(
            List<Widget> widgets,
            Point layoutOrigin,
            RecipeDisplayLayout.PopulatedSlot<EntryIngredient> populatedSlot
    ) {
        RecipeDisplayLayout.Slot layoutSlot = populatedSlot.slot();
        RecipeDisplayLayout.Position position = layoutSlot.position();
        Point point = new Point(layoutOrigin.x + position.x(), layoutOrigin.y + position.y());

        if (layoutSlot.background() == RecipeDisplayLayout.SlotBackground.OUTPUT) {
            widgets.add(Widgets.createResultSlotBackground(point));
        }

        Slot slot = Widgets.createSlot(point);

        if (layoutSlot.isInput()) {
            slot.markInput();
        } else {
            slot.disableBackground().markOutput();
        }

        populatedSlot.content().ifPresent(slot::entries);
        widgets.add(slot);

        return slot;
    }
}

package dev.mariany.genesis.compat.jei.client.category;

import com.mojang.serialization.Codec;
import dev.mariany.genesis.recipe.display.IdentifiedRecipeDisplay;
import dev.mariany.genesis.recipe.display.RecipeDisplayLayout;
import dev.mariany.genesis.recipe.display.RecipeDisplayViewport;
import dev.mariany.genesis.recipe.display.type.RecipeDisplayType;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public class JEIRecipeCategory<D extends IdentifiedRecipeDisplay> extends AbstractRecipeCategory<D> {
    private final RecipeDisplayType<D> displayType;
    private final RecipeDisplayViewport viewport;
    private final IDrawableStatic arrow;

    public JEIRecipeCategory(
            IGuiHelper guiHelper,
            IRecipeType<D> recipeType,
            RecipeDisplayType<D> displayType
    ) {
        this(guiHelper, recipeType, displayType, RecipeDisplayViewport.standard(displayType.category()));
    }

    private JEIRecipeCategory(
            IGuiHelper guiHelper,
            IRecipeType<D> recipeType,
            RecipeDisplayType<D> displayType,
            RecipeDisplayViewport viewport
    ) {
        super(
                recipeType,
                Component.translatable(displayType.category().titleTranslationKey()),
                guiHelper.createDrawableItemLike(displayType.category().icon().get()),
                viewport.width(),
                viewport.height()
        );

        this.displayType = displayType;
        this.viewport = viewport;
        this.arrow = guiHelper.getRecipeArrow();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, D display, IFocusGroup focuses) {
        this.displayType
                .category()
                .layout()
                .populate(this.displayType.recipeWidth(display), this.displayType.contents(display))
                .forEach(populatedSlot -> this.addSlot(builder, display, populatedSlot));
    }

    @Override
    public void draw(
            D display,
            IRecipeSlotsView recipeSlotsView,
            GuiGraphicsExtractor guiGraphicsExtractor,
            double mouseX,
            double mouseY
    ) {
        this.drawArrow(guiGraphicsExtractor);
    }

    private void drawArrow(GuiGraphicsExtractor guiGraphicsExtractor) {
        RecipeDisplayLayout.Position position = this.displayType.category().layout().arrowPosition();

        if (position == null) {
            return;
        }

        RecipeDisplayLayout.Position translatedPosition = this.viewport.translate(position);

        this.arrow.draw(guiGraphicsExtractor, translatedPosition.x(), translatedPosition.y());
    }

    @Override
    public Codec<D> getCodec(ICodecHelper codecHelper, IRecipeManager recipeManager) {
        return this.displayType.codec().codec();
    }

    @Override
    public Identifier getIdentifier(D display) {
        return display.identifier();
    }

    private void addSlot(
            IRecipeLayoutBuilder builder,
            D display,
            RecipeDisplayLayout.PopulatedSlot<SlotDisplay> populatedSlot
    ) {
        RecipeDisplayLayout.Slot slot = populatedSlot.slot();
        RecipeDisplayLayout.Position position = this.viewport.translate(slot.position());

        IRecipeSlotBuilder slotBuilder = slot.isInput()
                ? builder.addInputSlot(position.x(), position.y())
                : builder.addOutputSlot(position.x(), position.y());

        switch (slot.background()) {
            case STANDARD -> {
                if (this.hasStandardSlotBackground(display, slot)) {
                    slotBuilder.setStandardSlotBackground();
                }
            }
            case OUTPUT -> slotBuilder.setOutputSlotBackground();
        }

        populatedSlot.content().ifPresent(slotBuilder::add);
    }

    protected boolean hasStandardSlotBackground(D display, RecipeDisplayLayout.Slot slot) {
        return true;
    }

    protected RecipeDisplayType<D> displayType() {
        return this.displayType;
    }

    protected RecipeDisplayViewport viewport() {
        return this.viewport;
    }
}

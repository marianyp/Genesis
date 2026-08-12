package dev.mariany.genesis.client.gui.screen.ingame;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.client.gui.AssemblySlotTextures;
import dev.mariany.genesis.client.gui.screen.recipebook.AssemblyRecipeBookWidget;
import dev.mariany.genesis.client.gui.widget.ToggleableRecipeBookWidget;
import dev.mariany.genesis.item.custom.AssemblyPatternItem;
import dev.mariany.genesis.mixin.accessor.RecipeBookWidgetAccessor;
import dev.mariany.genesis.mixin.accessor.ScreenAccessor;
import dev.mariany.genesis.screen.AssemblyScreenHandler;
import dev.mariany.genesis.screen.slot.AssemblyInputSlot;
import dev.mariany.genesis.screen.slot.AssemblyPatternSlot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class AssemblyScreen extends AbstractRecipeBookScreen<AssemblyScreenHandler> {
    private static final Identifier TEXTURE = Genesis.id("textures/gui/container/assembly_table.png");
    private static final Component CAST_SLOT_TOOLTIP = Component.translatable(
            "container.genesis.assembly_table.cast_tooltip"
    );

    private final AssemblyRecipeBookWidget assemblyRecipeBookWidget;

    @Nullable
    private Item previousPattern = Items.AIR;

    public AssemblyScreen(AssemblyScreenHandler handler, Inventory inventory, Component title) {
        this(new AssemblyRecipeBookWidget(handler), handler, inventory, title);

    }

    private AssemblyScreen(
            AssemblyRecipeBookWidget assemblyRecipeBookWidget,
            AssemblyScreenHandler handler,
            Inventory inventory,
            Component title
    ) {
        super(handler, assemblyRecipeBookWidget, inventory, title);
        this.assemblyRecipeBookWidget = assemblyRecipeBookWidget;
    }

    @Override
    protected void init() {
        super.init();

        this.titleLabelX = 29;
        this.previousPattern = Items.AIR;

        this.menu.onAssemblyPatternChange(this::updateRecipeBookState);

        this.updateRecipeBookState();
    }

    private void updateRecipeBookState() {
        getRecipeBook().ifPresent(
                toggleableRecipeBookWidget -> {
                    Optional<AssemblyPatternItem> optionalAssemblyPatternItem = this.menu.getAssemblyPatternItem();
                    boolean enabled = optionalAssemblyPatternItem.isPresent();

                    toggleableRecipeBookWidget.setEnabled(enabled);

                    Item pattern = optionalAssemblyPatternItem.map(Item::asItem).orElse(Items.AIR);

                    if (pattern != this.previousPattern) {
                        ((RecipeBookWidgetAccessor) this.assemblyRecipeBookWidget).genesis$ghostRecipe().clear();
                        this.previousPattern = pattern;
                    }

                    if (enabled || !this.assemblyRecipeBookWidget.isVisible()) {
                        return;
                    }

                    this.assemblyRecipeBookWidget.close();

                    this.leftPos = this.assemblyRecipeBookWidget.updateScreenPosition(this.width, this.imageWidth);

                    ScreenPosition buttonPos = this.getRecipeBookButtonPosition();
                    toggleableRecipeBookWidget.setPosition(buttonPos.x(), buttonPos.y());

                    this.onRecipeBookButtonClick();
                }
        );
    }

    private Optional<ToggleableRecipeBookWidget> getRecipeBook() {
        for (Renderable drawable : ((ScreenAccessor) this).genesis$renderables()) {
            if (drawable instanceof ToggleableRecipeBookWidget toggleableRecipeBookWidget) {
                return Optional.of(toggleableRecipeBookWidget);
            }
        }

        return Optional.empty();
    }

    @Override
    protected ScreenPosition getRecipeBookButtonPosition() {
        return new ScreenPosition(this.leftPos + 5, this.height / 2 - 49);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
        super.extractBackground(context, mouseX, mouseY, deltaTicks);
        int y = (this.height - this.imageHeight) / 2;

        context.blit(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                this.leftPos,
                y,
                0F,
                0F,
                this.imageWidth,
                this.imageHeight,
                256,
                256
        );
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
        super.extractRenderState(context, mouseX, mouseY, deltaTicks);
        this.renderSlotTooltip(context, mouseX, mouseY);
    }

    private void renderSlotTooltip(GuiGraphicsExtractor context, int mouseX, int mouseY) {
        if (!(this.hoveredSlot instanceof AssemblyPatternSlot assemblyPatternSlot) || !assemblyPatternSlot.isEmpty()) {
            return;
        }

        context.setTooltipForNextFrame(
                this.font,
                this.font.split(CAST_SLOT_TOOLTIP, 115),
                mouseX,
                mouseY
        );
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor context, int mouseX, int mouseY) {
        super.extractLabels(context, mouseX, mouseY);

        if (this.menu.getCraftingPattern().isEmpty()) {
            return;
        }

        for (Slot slot : this.menu.slots) {
            if (slot instanceof AssemblyInputSlot assemblyInputSlot) {
                this.drawSlot(context, assemblyInputSlot, assemblyInputSlot.isActive());
            }
        }
    }

    private void drawSlot(GuiGraphicsExtractor context, AssemblyInputSlot slot, boolean enabled) {
        AssemblySlotTextures.draw(context, slot.getContainerSlot(), !enabled, slot.x, slot.y);
    }
}

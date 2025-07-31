package dev.mariany.genesis.client.gui.screen.ingame;

import dev.mariany.genesis.Genesis;
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
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.ScreenPos;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class AssemblyScreen extends RecipeBookScreen<AssemblyScreenHandler> {
    private static final Identifier TEXTURE = Genesis.id("textures/gui/container/assembly_table.png");
    private static final Identifier SLOT_TEXTURE = Genesis.id("container/assembly/slots");
    private static final Identifier DISABLED_SLOT_TEXTURE = Genesis.id("container/assembly/locked_slots");

    private static final Text CAST_SLOT_TOOLTIP = Text.translatable(
            "container.genesis.assembly_table.cast_tooltip"
    );

    private static final int SLOTS_PER_ROW = 3;
    private static final int SLOTS_TEXTURE_SIZE = 54;
    private static final int SLOT_SIZE = 18;

    private final AssemblyRecipeBookWidget assemblyRecipeBookWidget;

    @Nullable
    private Item previousPattern = Items.AIR;

    public AssemblyScreen(AssemblyScreenHandler handler, PlayerInventory inventory, Text title) {
        this(new AssemblyRecipeBookWidget(handler), handler, inventory, title);

    }

    private AssemblyScreen(
            AssemblyRecipeBookWidget assemblyRecipeBookWidget,
            AssemblyScreenHandler handler,
            PlayerInventory inventory,
            Text title
    ) {
        super(handler, assemblyRecipeBookWidget, inventory, title);
        this.assemblyRecipeBookWidget = assemblyRecipeBookWidget;
    }

    @Override
    protected void init() {
        super.init();

        this.titleX = 29;
        this.previousPattern = Items.AIR;

        this.handler.onAssemblyPatternChange(inventory -> updateRecipeBookState());
        this.updateRecipeBookState();
    }

    private void updateRecipeBookState() {
        getRecipeBook().ifPresent(
                toggleableRecipeBookWidget -> {
                    Optional<AssemblyPatternItem> optionalAssemblyPatternItem = this.handler.getAssemblyPatternItem();
                    boolean enabled = optionalAssemblyPatternItem.isPresent();

                    toggleableRecipeBookWidget.setEnabled(enabled);

                    Item pattern = optionalAssemblyPatternItem.map(Item::asItem).orElse(Items.AIR);

                    if (pattern != this.previousPattern) {
                        ((RecipeBookWidgetAccessor) this.assemblyRecipeBookWidget).genesis$ghostRecipe().clear();
                        this.previousPattern = pattern;
                    }

                    if (!enabled && this.assemblyRecipeBookWidget.isOpen()) {
                        this.assemblyRecipeBookWidget.close();
                        this.x = this.assemblyRecipeBookWidget.findLeftEdge(this.width, this.backgroundWidth);
                        ScreenPos buttonPos = this.getRecipeBookButtonPos();
                        toggleableRecipeBookWidget.setPosition(buttonPos.x(), buttonPos.y());
                        this.onRecipeBookToggled();
                    }
                }
        );
    }

    private Optional<ToggleableRecipeBookWidget> getRecipeBook() {
        for (Drawable drawable : ((ScreenAccessor) this).genesis$drawables()) {
            if (drawable instanceof ToggleableRecipeBookWidget toggleableRecipeBookWidget) {
                return Optional.of(toggleableRecipeBookWidget);
            }
        }

        return Optional.empty();
    }

    @Override
    protected ScreenPos getRecipeBookButtonPos() {
        return new ScreenPos(this.x + 5, this.height / 2 - 49);
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int y = (this.height - this.backgroundHeight) / 2;

        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                TEXTURE,
                this.x,
                y,
                0F,
                0F,
                this.backgroundWidth,
                this.backgroundHeight,
                256,
                256
        );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.renderSlotTooltip(context, mouseX, mouseY);
    }

    private void renderSlotTooltip(DrawContext context, int mouseX, int mouseY) {
        if (this.focusedSlot instanceof AssemblyPatternSlot assemblyPatternSlot && assemblyPatternSlot.isEmpty()) {
            context.drawOrderedTooltip(
                    this.textRenderer,
                    this.textRenderer.wrapLines(CAST_SLOT_TOOLTIP, 115),
                    mouseX,
                    mouseY
            );
        }
    }

    @Override
    public void drawSlot(DrawContext context, Slot slot) {
        boolean shouldDraw = true;

        if (slot instanceof AssemblyInputSlot assemblyInputSlot) {
            boolean canInsert = assemblyInputSlot.canInsert();

            if (this.handler.getAssemblyPatternItem().isPresent()) {
                this.drawBackground(context, assemblyInputSlot, canInsert);
            }

            if (!canInsert) {
                shouldDraw = false;
            }
        }

        if (shouldDraw) {
            super.drawSlot(context, slot);
        }
    }

    private void drawBackground(DrawContext context, AssemblyInputSlot slot, boolean enabled) {
        Identifier texture = enabled ? SLOT_TEXTURE : DISABLED_SLOT_TEXTURE;

        int index = slot.getIndex();

        int u = (index % SLOTS_PER_ROW) * SLOT_SIZE;
        int v = (index / SLOTS_PER_ROW) * SLOT_SIZE;

        context.drawGuiTexture(
                RenderPipelines.GUI_TEXTURED,
                texture,
                SLOTS_TEXTURE_SIZE,
                SLOTS_TEXTURE_SIZE,
                u,
                v,
                slot.x - 1,
                slot.y - 1,
                SLOT_SIZE,
                SLOT_SIZE
        );
    }
}

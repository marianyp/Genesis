package dev.mariany.genesis.client.gui.screen.ingame;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.client.gui.screen.recipebook.KilnRecipeBookWidget;
import dev.mariany.genesis.screen.KilnScreenHandler;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenPos;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.recipebook.RecipeBookType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeBookCategories;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class KilnScreen extends RecipeBookScreen<KilnScreenHandler> {
    private static final Identifier LIT_PROGRESS_TEXTURE = Identifier.ofVanilla("container/furnace/lit_progress");
    private static final Identifier BURN_PROGRESS_TEXTURE = Identifier.ofVanilla("container/furnace/burn_progress");
    private static final Identifier BACKGROUND = Genesis.id("textures/gui/container/kiln.png");
    private static final Text TOGGLE_SMELTABLE_TEXT = Text.translatable("gui.recipebook.toggleRecipes.smeltable");
    private static final List<RecipeBookWidget.Tab> TABS = List.of(
            new RecipeBookWidget.Tab(RecipeBookType.FURNACE),
            new RecipeBookWidget.Tab(Items.PORKCHOP, RecipeBookCategories.FURNACE_FOOD),
            new RecipeBookWidget.Tab(Items.STONE, RecipeBookCategories.FURNACE_BLOCKS),
            new RecipeBookWidget.Tab(Items.LAVA_BUCKET, Items.EMERALD, RecipeBookCategories.FURNACE_MISC)
    );

    public KilnScreen(KilnScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, new KilnRecipeBookWidget(handler, TOGGLE_SMELTABLE_TEXT, TABS), inventory, title);
    }

    @Override
    protected ScreenPos getRecipeBookButtonPos() {
        return new ScreenPos(this.x + 20, this.height / 2 - 49);
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int x = this.x;
        int y = this.y;

        // Draw Background
        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                BACKGROUND,
                x,
                y,
                0.0F,
                0.0F,
                this.backgroundWidth,
                this.backgroundHeight,
                256,
                256
        );

        // Draw Fire
        if (this.handler.isBurning()) {
            context.drawGuiTexture(
                    RenderPipelines.GUI_TEXTURED,
                    LIT_PROGRESS_TEXTURE,
                    14,
                    14,
                    0,
                    0,
                    x + 56,
                    y + 53,
                    14,
                    14
            );
        }

        // Draw Progress
        int progress = MathHelper.ceil(this.handler.getCookProgress() * 24F);
        context.drawGuiTexture(
                RenderPipelines.GUI_TEXTURED,
                BURN_PROGRESS_TEXTURE,
                24,
                16,
                0,
                0,
                x + 79,
                y + 34,
                progress,
                16
        );
    }
}

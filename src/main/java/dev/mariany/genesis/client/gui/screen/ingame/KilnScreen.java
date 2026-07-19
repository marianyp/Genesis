package dev.mariany.genesis.client.gui.screen.ingame;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.client.gui.screen.recipebook.KilnRecipeBookWidget;
import dev.mariany.genesis.screen.KilnScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeBookCategories;

import java.util.List;

@Environment(EnvType.CLIENT)
public class KilnScreen extends AbstractRecipeBookScreen<KilnScreenHandler> {
    private static final Identifier LIT_PROGRESS_TEXTURE =
            Identifier.withDefaultNamespace("container/furnace/lit_progress");

    private static final Identifier BURN_PROGRESS_TEXTURE =
            Identifier.withDefaultNamespace("container/furnace/burn_progress");

    private static final Identifier BACKGROUND = Genesis.id("textures/gui/container/kiln.png");

    private static final Component TOGGLE_SMELTABLE_TEXT =
            Component.translatable("gui.recipebook.toggleRecipes.smeltable");

    private static final List<RecipeBookComponent.TabInfo> TABS = List.of(
            new RecipeBookComponent.TabInfo(SearchRecipeBookCategory.FURNACE),
            new RecipeBookComponent.TabInfo(Items.PORKCHOP, RecipeBookCategories.FURNACE_FOOD),
            new RecipeBookComponent.TabInfo(Items.STONE, RecipeBookCategories.FURNACE_BLOCKS),
            new RecipeBookComponent.TabInfo(Items.LAVA_BUCKET, Items.EMERALD, RecipeBookCategories.FURNACE_MISC)
    );

    public KilnScreen(KilnScreenHandler handler, Inventory inventory, Component title) {
        super(handler, new KilnRecipeBookWidget(handler, TOGGLE_SMELTABLE_TEXT, TABS), inventory, title);
    }

    @Override
    protected ScreenPosition getRecipeBookButtonPosition() {
        return new ScreenPosition(this.leftPos + 20, this.height / 2 - 49);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
        super.extractBackground(context, mouseX, mouseY, deltaTicks);
        int x = this.leftPos;
        int y = this.topPos;

        // Draw Background
        context.blit(
                RenderPipelines.GUI_TEXTURED,
                BACKGROUND,
                x,
                y,
                0.0F,
                0.0F,
                this.imageWidth,
                this.imageHeight,
                256,
                256
        );

        // Draw Fire
        if (this.menu.isBurning()) {
            context.blitSprite(
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
        int progress = Mth.ceil(this.menu.getCookProgress() * 24F);

        context.blitSprite(
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

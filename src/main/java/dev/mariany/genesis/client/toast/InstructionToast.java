package dev.mariany.genesis.client.toast;

import dev.mariany.genesis.Genesis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class InstructionToast implements HideableToast {
    private static final Identifier TEXTURE = Genesis.id("toast/instruction");
    private static final Identifier ICON_TEXTURE = Genesis.id("toast/information");

    private static final int ICON_SIZE = 8;
    private static final int HALF_ICON_SIZE = ICON_SIZE / 2;

    private static final int MAX_TEXT_ROWS = 2;
    private static final int TEXT_COLOR = 0xFF000000;
    private static final int TEXT_LINE_HEIGHT = 11;
    private static final int TEXT_WIDTH = 126;
    private static final int TOAST_PADDING_BOTTOM = 3;
    private static final int TOAST_PADDING_TOP = 7;

    private Toast.Visibility visibility = Toast.Visibility.SHOW;
    private final ItemStack icon;
    private final List<OrderedText> text;

    public InstructionToast(TextRenderer textRenderer, ItemStack icon, Text title, @Nullable Text description) {
        this.icon = icon;

        this.text = new ArrayList<>(MAX_TEXT_ROWS);
        this.text.addAll(textRenderer.wrapLines(title.copy().withColor(Colors.PURPLE), TEXT_WIDTH));

        if (description != null) {
            this.text.addAll(textRenderer.wrapLines(description, TEXT_WIDTH));
        }
    }

    @Override
    public Toast.Visibility getVisibility() {
        return this.visibility;
    }

    public void hide() {
        this.visibility = Toast.Visibility.HIDE;
    }

    @Override
    public void update(ToastManager manager, long time) {
    }

    @Override
    public int getHeight() {
        return TOAST_PADDING_TOP + this.getTextHeight() + TOAST_PADDING_BOTTOM;
    }

    private int getTextHeight() {
        return Math.max(this.text.size(), MAX_TEXT_ROWS) * TEXT_LINE_HEIGHT;
    }

    @Override
    public void draw(DrawContext context, TextRenderer textRenderer, long startTime) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, this.getWidth(), this.getHeight());

        context.drawGuiTexture(
                RenderPipelines.GUI_TEXTURED,
                ICON_TEXTURE,
                -HALF_ICON_SIZE,
                1,
                ICON_SIZE,
                ICON_SIZE
        );

        int totalTextHeight = this.text.size() * TEXT_LINE_HEIGHT;
        int verticalTextOffset = TOAST_PADDING_TOP + (this.getTextHeight() - totalTextHeight) / 2;

        for (int lineIndex = 0; lineIndex < this.text.size(); lineIndex++) {
            int y = verticalTextOffset + lineIndex * TEXT_LINE_HEIGHT;

            context.drawText(
                    textRenderer,
                    this.text.get(lineIndex),
                    30,
                    y,
                    TEXT_COLOR,
                    false
            );
        }

        context.drawItemWithoutEntity(this.icon, 8, 8);
    }
}

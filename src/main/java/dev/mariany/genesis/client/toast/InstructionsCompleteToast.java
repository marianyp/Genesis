package dev.mariany.genesis.client.toast;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.sound.GenesisSoundEvents;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class InstructionsCompleteToast implements HideableToast {
    private static final Identifier TEXTURE = Genesis.id("toast/instruction");
    private static final Identifier INFORMATION_ICON_TEXTURE = Genesis.id("toast/information");
    private static final Identifier COMPLETE_ICON_TEXTURE = Genesis.id("toast/complete");

    private static final int BAR_COLOR = 0xFF00AA00;

    private static final int INFORMATION_ICON_SIZE = 8;
    private static final int HALF_INFORMATION_ICON_SIZE = INFORMATION_ICON_SIZE / 2;

    private static final int COMPLETE_ICON_SIZE = 16;
    private static final int HALF_COMPLETE_ICON_SIZE = COMPLETE_ICON_SIZE / 2;

    private static final int TITLE_X = COMPLETE_ICON_SIZE * 2;
    private static final int TITLE_Y = 12;

    private static final Text TITLE = Text.translatable("instruction.genesis.complete.title");

    private final int displayDuration;
    private long lastTime;
    private float lastProgress;
    private Visibility visibility = Visibility.SHOW;

    public InstructionsCompleteToast() {
        this(8000);
    }

    public InstructionsCompleteToast(int displayDuration) {
        this.displayDuration = displayDuration;
    }

    @Override
    public SoundEvent getSoundEvent() {
        return GenesisSoundEvents.UI_TOAST_INSTRUCTIONS_COMPLETE;
    }

    @Override
    public Visibility getVisibility() {
        return this.visibility;
    }

    @Override
    public void hide() {
        this.visibility = Visibility.HIDE;
    }

    @Override
    public void update(ToastManager manager, long time) {
        float progress = Math.min((float) time / this.displayDuration, 1.0F);

        this.lastProgress = MathHelper.clampedLerp(
                this.lastProgress,
                progress,
                (time - this.lastTime) / 100F
        );
        this.lastTime = time;

        if (time >= this.displayDuration) {
            this.visibility = Toast.Visibility.HIDE;
        }
    }

    @Override
    public void draw(DrawContext context, TextRenderer textRenderer, long startTime) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, 0, 0, this.getWidth(), this.getHeight());

        context.drawGuiTexture(
                RenderPipelines.GUI_TEXTURED,
                INFORMATION_ICON_TEXTURE,
                -HALF_INFORMATION_ICON_SIZE,
                1,
                INFORMATION_ICON_SIZE,
                INFORMATION_ICON_SIZE
        );

        context.drawGuiTexture(
                RenderPipelines.GUI_TEXTURED,
                COMPLETE_ICON_TEXTURE,
                HALF_COMPLETE_ICON_SIZE,
                HALF_COMPLETE_ICON_SIZE,
                COMPLETE_ICON_SIZE,
                COMPLETE_ICON_SIZE
        );

        context.drawText(textRenderer, TITLE, TITLE_X, TITLE_Y, Colors.PURPLE, false);

        this.drawProgressBar(context);
    }

    private void drawProgressBar(DrawContext context) {
        int barY = this.getHeight() - 4;
        context.fill(3, barY, 157, barY + 1, Colors.WHITE);
        context.fill(3, barY, (int) (3.0F + 154.0F * this.lastProgress), barY + 1, BAR_COLOR);
    }
}

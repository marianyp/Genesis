package dev.mariany.genesis.client.gui.screen.tiredness;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.attachment.GenesisAttachmentTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.function.Supplier;

public class TirednessWidget extends AbstractWidget {
    private static final Identifier SPRITE = Genesis.id("container/inventory/tiredness/tiredness");
    private static final Identifier SPRITE_HOVER = Genesis.id("container/inventory/tiredness/tiredness_highlighted");

    private final Supplier<Point> pointSupplier;

    public TirednessWidget(Supplier<Point> pointSupplier) {
        super(0, 0, 12, 12, Component.empty());

        this.pointSupplier = pointSupplier;

        this.updatePosition();
    }

    @Override
    public Component getMessage() {
        return getComponent();
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        if (isHidden()) {
            this.setTooltip(null);
            return;
        }

        this.update(graphics);
    }

    private static boolean isHidden() {
        return getMinimumTicksBeforeSleeping() <= 0;
    }

    private void update(GuiGraphicsExtractor graphics) {
        this.updatePosition();
        this.setTooltip(createTooltip());
        this.blitSprite(graphics);
    }

    private void updatePosition() {
        Point point = pointSupplier.get();
        int x = (int) point.getX();
        int y = (int) point.getY();
        this.setPosition(x, y);
    }

    private void blitSprite(GuiGraphicsExtractor graphics) {
        Identifier icon = this.isHovered() ? SPRITE_HOVER : SPRITE;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, icon, this.getX(), this.getY(), this.width, this.height);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
    }

    @Override
    protected boolean isValidClickButton(MouseButtonInfo buttonInfo) {
        return false;
    }

    private static Tooltip createTooltip() {
        return Tooltip.create(getComponent());
    }

    private static Component getComponent() {
        Player player = Minecraft.getInstance().player;
        return getComponent(player);
    }

    private static Component getComponent(@Nullable Player player) {
        float progress = player == null ? 0 : getProgress(player);
        int percentage = Mth.floor(progress * 100);
        return Component.translatable("gui.genesis.tiredness", percentage);
    }

    private static float getProgress(@NotNull Player player) {
        int minimumTicksBeforeSleeping = getMinimumTicksBeforeSleeping();

        if (minimumTicksBeforeSleeping <= 0) {
            return 1;
        }

        return (float) getAwakeTicks(player) / minimumTicksBeforeSleeping;
    }

    private static int getMinimumTicksBeforeSleeping() {
        return Genesis.TIREDNESS_LOGIC.getMinimumTicksBeforeSleeping();
    }

    private static int getAwakeTicks(Player player) {
        return player.getAttachedOrElse(GenesisAttachmentTypes.AWAKE_TICKS, 0);
    }
}

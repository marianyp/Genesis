package dev.mariany.genesis.client.gui;

import dev.mariany.genesis.Genesis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public final class AssemblySlotTextures {
    private static final Identifier SLOT_TEXTURE = Genesis.id("container/assembly/slots");
    private static final Identifier LOCKED_SLOT_TEXTURE = Genesis.id("container/assembly/locked_slots");

    private static final int SLOTS_PER_ROW = 3;
    private static final int SLOTS_TEXTURE_SIZE = 54;
    private static final int SLOT_SIZE = 18;

    private AssemblySlotTextures() {
    }

    public static void draw(GuiGraphicsExtractor graphics, int slotIndex, boolean locked, int x, int y) {
        Identifier texture = locked ? LOCKED_SLOT_TEXTURE : SLOT_TEXTURE;

        int u = (slotIndex % SLOTS_PER_ROW) * SLOT_SIZE;
        int v = (slotIndex / SLOTS_PER_ROW) * SLOT_SIZE;

        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                texture,
                SLOTS_TEXTURE_SIZE,
                SLOTS_TEXTURE_SIZE,
                u,
                v,
                x - 1,
                y - 1,
                SLOT_SIZE,
                SLOT_SIZE
        );
    }
}

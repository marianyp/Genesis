package dev.mariany.genesis.mixin.accessor;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {
    @Accessor("leftPos")
    int genesis$leftPos();

    @Accessor("topPos")
    int genesis$topPos();

    @Accessor("leftPos")
    void genesis$setLeftPos(int leftPos);

    @Accessor("imageWidth")
    int genesis$imageWidth();
}

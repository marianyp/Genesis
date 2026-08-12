package dev.mariany.genesis.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.genesis.client.event.RecipeBookScreenEvents;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractRecipeBookScreen.class)
public abstract class RecipeBookScreenMixin {
    @WrapOperation(
            method = "initButton",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/AbstractRecipeBookScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;"
            )
    )
    private <T extends GuiEventListener & Renderable & NarratableEntry> GuiEventListener wrapAddRenderableWidget(
            AbstractRecipeBookScreen<?> screen,
            T button,
            Operation<T> original
    ) {
        Renderable widget;

        if (button instanceof AbstractWidget abstractWidget) {
            widget = RecipeBookScreenEvents.MODIFY_BUTTON.invoker().modify(screen, abstractWidget);
        } else {
            widget = button;
        }

        return original.call(screen, widget);
    }
}

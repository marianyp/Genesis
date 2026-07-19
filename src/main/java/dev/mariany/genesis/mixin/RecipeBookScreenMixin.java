package dev.mariany.genesis.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.genesis.client.gui.screen.ingame.AssemblyScreen;
import dev.mariany.genesis.client.gui.widget.ToggleableRecipeBookWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractRecipeBookScreen.class)
public abstract class RecipeBookScreenMixin<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    public RecipeBookScreenMixin(T handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Shadow
    protected abstract ScreenPosition getRecipeBookButtonPosition();

    @Shadow
    @Final
    private RecipeBookComponent<?> recipeBookComponent;

    @Shadow
    protected abstract void onRecipeBookButtonClick();

    @WrapOperation(
            method = "initButton",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/AbstractRecipeBookScreen;addRenderableWidget(Lnet/minecraft/client/gui/components/events/GuiEventListener;)Lnet/minecraft/client/gui/components/events/GuiEventListener;"
            )
    )
    private GuiEventListener wrapAddRecipeBook(AbstractRecipeBookScreen<?> screen, GuiEventListener element, Operation<GuiEventListener> original) {
        ScreenPosition screenPos = this.getRecipeBookButtonPosition();

        if (((AbstractRecipeBookScreen<?>) (Object) this) instanceof AssemblyScreen) {
            return original.call(screen, new ToggleableRecipeBookWidget(
                            screenPos.x(),
                            screenPos.y(),
                            button -> {
                                this.recipeBookComponent.toggleVisibility();
                                this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);

                                ScreenPosition buttonPos = this.getRecipeBookButtonPosition();
                                button.setPosition(buttonPos.x(), buttonPos.y());

                                this.onRecipeBookButtonClick();
                            }
                    )
            );
        }

        return original.call(screen, element);
    }
}

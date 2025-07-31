package dev.mariany.genesis.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.genesis.client.gui.screen.ingame.AssemblyScreen;
import dev.mariany.genesis.client.gui.widget.ToggleableRecipeBookWidget;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenPos;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RecipeBookScreen.class)
public abstract class RecipeBookScreenMixin<T extends ScreenHandler> extends HandledScreen<T> {
    public RecipeBookScreenMixin(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Shadow
    protected abstract ScreenPos getRecipeBookButtonPos();

    @Shadow
    @Final
    private RecipeBookWidget<?> recipeBook;

    @Shadow
    protected abstract void onRecipeBookToggled();

    @WrapOperation(
            method = "addRecipeBook",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/ingame/RecipeBookScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"
            )
    )
    private Element wrapAddRecipeBook(RecipeBookScreen<?> screen, Element element, Operation<Element> original) {
        ScreenPos screenPos = this.getRecipeBookButtonPos();

        if (((RecipeBookScreen<?>) (Object) this) instanceof AssemblyScreen) {
            return original.call(screen, new ToggleableRecipeBookWidget(
                            screenPos.x(),
                            screenPos.y(),
                            button -> {
                                this.recipeBook.toggleOpen();
                                this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);

                                ScreenPos buttonPos = this.getRecipeBookButtonPos();
                                button.setPosition(buttonPos.x(), buttonPos.y());

                                this.onRecipeBookToggled();
                            }
                    )
            );
        }

        return original.call(screen, element);
    }
}

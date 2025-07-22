package dev.mariany.genesis.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.genesis.client.age.ClientAgeManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.RecipeToast;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(RecipeToast.class)
public class RecipeToastMixin {
    @Shadow
    @Final
    private List<?> displayItems;

    // Prevent showing recipe for item that's locked
    @WrapOperation(
            method = "show",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/toast/RecipeToast;addRecipes(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)V"
            )
    )
    private static void wrapShow(RecipeToast recipeToast, ItemStack categoryItem, ItemStack unlockedItem, Operation<Void> original) {
        if (ClientAgeManager.getInstance().isUnlocked(unlockedItem)) {
            original.call(recipeToast, categoryItem, unlockedItem);
        }
    }

    // Prevent calling this.displayItems.get when there aren't any (all locked instructions)
    @Inject(method = "draw", at = @At(value = "HEAD"), cancellable = true)
    public void injectDraw(DrawContext context, TextRenderer textRenderer, long startTime, CallbackInfo ci) {
        if (displayItems.isEmpty()) {
            ci.cancel();
        }
    }
}

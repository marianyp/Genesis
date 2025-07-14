package dev.mariany.genesis.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.genesis.client.age.ClientAgeManager;
import net.minecraft.client.toast.RecipeToast;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RecipeToast.class)
public class RecipeToastMixin {
    @WrapOperation(
            method = "show",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/toast/RecipeToast;addRecipes(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)V"
            )
    )
    private static void show(RecipeToast recipeToast, ItemStack categoryItem, ItemStack unlockedItem, Operation<Void> original) {
        if (ClientAgeManager.getInstance().isUnlocked(unlockedItem)) {
            original.call(recipeToast, categoryItem, unlockedItem);
        }
    }
}

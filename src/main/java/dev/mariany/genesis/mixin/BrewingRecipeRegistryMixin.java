package dev.mariany.genesis.mixin;

import dev.mariany.genesis.event.brewing.BrewingEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionBrewing.class)
public class BrewingRecipeRegistryMixin {
    @Inject(method = "isContainer", at = @At(value = "HEAD"), cancellable = true)
    private void injectIsContainer(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!BrewingEvents.ALLOW_INGREDIENT.invoker().allow(stack.getItem())) {
            return;
        }

        cir.setReturnValue(true);
    }

    @Inject(method = "mix", at = @At(value = "HEAD"), cancellable = true)
    public void injectMix(ItemStack ingredient, ItemStack input, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack result = BrewingEvents.MIX.invoker().mix(ingredient, input);

        if (result == null) {
            return;
        }

        cir.setReturnValue(result);
    }
}

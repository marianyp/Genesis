package dev.mariany.genesis.mixin;

import dev.mariany.genesis.event.brewing.BrewingEvents;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.BrewingStandMenu$PotionSlot")
public class BrewingStandMenuPotionSlotMixin {
    @Inject(method = "mayPlaceItem", at = @At(value = "HEAD"), cancellable = true)
    private static void matches(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!BrewingEvents.ALLOW_INGREDIENT.invoker().allow(stack.getItem())) {
            return;
        }

        cir.setReturnValue(true);
    }
}

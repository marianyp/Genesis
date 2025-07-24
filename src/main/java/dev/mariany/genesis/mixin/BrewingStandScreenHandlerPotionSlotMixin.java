package dev.mariany.genesis.mixin;

import dev.mariany.genesis.recipe.brew.GenesisBrewingRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.BrewingStandScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingStandScreenHandler.PotionSlot.class)
public class BrewingStandScreenHandlerPotionSlotMixin {
    @Inject(method = "matches", at = @At(value = "HEAD"), cancellable = true)
    private static void matches(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (GenesisBrewingRecipes.getPotionBypasses().contains(stack.getItem())) {
            cir.setReturnValue(true);
        }
    }
}

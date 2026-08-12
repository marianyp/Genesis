package dev.mariany.genesis.mixin;

import dev.mariany.genesis.event.brewing.BrewingEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionBrewing.Builder.class)
public class BrewingRecipeRegistryBuilderMixin {
    @Inject(method = "expectPotion", at = @At(value = "HEAD"), cancellable = true)
    private static void injectExpectPotion(Item potionType, CallbackInfo ci) {
        if (BrewingEvents.ALLOW_INGREDIENT.invoker().allow(potionType)) {
            ci.cancel();
        }
    }
}

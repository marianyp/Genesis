package dev.mariany.genesis.mixin;

import dev.mariany.genesis.event.brewing.BrewingEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandBlockEntityMixin {
    @Inject(method = "canPlaceItem", at = @At(value = "HEAD"), cancellable = true)
    public void injectIsValid(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        BrewingStandBlockEntity brewingStandBlockEntity = (BrewingStandBlockEntity) (Object) this;

        boolean allowed = BrewingEvents.ALLOW_INPUT.invoker().allow(
                brewingStandBlockEntity,
                slot,
                stack
        );

        if (!allowed) {
            return;
        }

        cir.setReturnValue(true);
    }
}

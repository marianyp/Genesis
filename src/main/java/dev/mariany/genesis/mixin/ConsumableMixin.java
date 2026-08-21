package dev.mariany.genesis.mixin;

import dev.mariany.genesis.event.entity.EntityEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Consumable.class)
public class ConsumableMixin {
    @Inject(method = "onConsume", at = @At(value = "HEAD"))
    public void injectOnConsume(
            Level level,
            LivingEntity livingEntity,
            ItemStack stack,
            CallbackInfoReturnable<ItemStack> cir
    ) {
        EntityEvents.AFTER_ENTITY_CONSUME.invoker().onEntityConsume(livingEntity, stack);
    }

    @Inject(method = "canConsume", at = @At(value = "HEAD"), cancellable = true)
    public void injectCanConsume(LivingEntity livingEntity, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!EntityEvents.ALLOW_ENTITY_CONSUME.invoker().allow(livingEntity, stack)) {
            return;
        }

        cir.setReturnValue(true);
    }
}

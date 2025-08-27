package dev.mariany.genesis.mixin;

import dev.mariany.genesis.event.entity.EntityEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "onDeath", at = @At(value = "HEAD"))
    public void injectOnDeath(DamageSource damageSource, CallbackInfo ci) {
        EntityEvents.BEFORE_ENTITY_DEATH.invoker().onEntityDeath((LivingEntity) (Object) this);
    }
}

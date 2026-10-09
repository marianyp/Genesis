package dev.mariany.genesis.world.effect;

import dev.mariany.genesis.attachment.GenesisAttachmentTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class PoorlyRestedMobEffect extends MobEffect {
    protected PoorlyRestedMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public float getReductionFactor() {
        return 0.5F;
    }

    @Override
    public void onMobRemoved(
            ServerLevel serverLevel,
            LivingEntity livingEntity,
            int amplifier,
            Entity.RemovalReason reason
    ) {
        if (reason != Entity.RemovalReason.KILLED || !(livingEntity instanceof ServerPlayer)) {
            return;
        }

        List<MobEffectInstance> poorlyRestedEffects = livingEntity.getAttached(
                GenesisAttachmentTypes.POORLY_RESTED_EFFECTS
        );

        if (poorlyRestedEffects != null && !poorlyRestedEffects.isEmpty()) {
            return;
        }

        List<MobEffectInstance> updatedPoorlyRestedEffects = new ArrayList<>();

        for (MobEffectInstance mobEffectInstance : livingEntity.getActiveEffects()) {
            if (mobEffectInstance.getEffect().value() instanceof PoorlyRestedMobEffect) {
                updatedPoorlyRestedEffects.add(mobEffectInstance);
            }
        }

        if (updatedPoorlyRestedEffects.isEmpty()) {
            return;
        }

        livingEntity.setAttached(GenesisAttachmentTypes.POORLY_RESTED_EFFECTS, updatedPoorlyRestedEffects);
    }
}

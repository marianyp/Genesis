package dev.mariany.genesis.world.effect;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.particle.GenesisParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class SolaceMobEffect extends MobEffect {
    protected SolaceMobEffect(final MobEffectCategory category, final int color) {
        super(category, color, GenesisParticleTypes.SOLACE);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplification) {
        return true;
    }

    @Override
    public boolean applyEffectTick(final ServerLevel level, final LivingEntity livingEntity, final int amplification) {
        if (Genesis.SOLACE_LOGIC.isHealingRestricted(livingEntity)) {
            return false;
        }

        int tickCount = livingEntity.tickCount;

        if (shouldHealThisTick(tickCount, amplification)) {
            healEntity(livingEntity);
        }

        return true;
    }

    private static void healEntity(LivingEntity livingEntity) {
        if (livingEntity.getHealth() >= livingEntity.getMaxHealth()) {
            return;
        }

        livingEntity.heal(1);
    }

    private static boolean shouldHealThisTick(final int tickCount, final int amplification) {
        int interval = 50 >> amplification;
        return interval == 0 || tickCount % interval == 0;
    }
}


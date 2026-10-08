package dev.mariany.genesis.world.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class PoorlyRestedMobEffect extends MobEffect {
    protected PoorlyRestedMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public float getReductionFactor() {
        return 0.5F;
    }
}

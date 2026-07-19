package dev.mariany.genesis.world.effect;

import dev.mariany.genesis.Genesis;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class GenesisMobEffects {
    public static final Holder<MobEffect> SOLACE = register(
            "solace",
            new SolaceMobEffect(MobEffectCategory.BENEFICIAL, 13458603)
    );

    private static Holder<MobEffect> register(final String name, final MobEffect mobEffect) {
        return Registry.registerForHolder(
                BuiltInRegistries.MOB_EFFECT,
                Genesis.id(name),
                mobEffect
        );
    }

    public static void bootstrap() {
        Genesis.bootstrapLog("Mob Effects");
    }
}

package dev.mariany.genesis.component.type;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.RemoveStatusEffectsConsumeEffect;

public class GenesisConsumableComponents {
    public static final Consumable ENCHANTED_HONEY_BOTTLE = Consumables
            .defaultDrink()
            .consumeSeconds(2F)
            .sound(SoundEvents.HONEY_DRINK)
            .onConsume(new RemoveStatusEffectsConsumeEffect(MobEffects.POISON))
            .onConsume(new RemoveStatusEffectsConsumeEffect(MobEffects.WITHER))
            .onConsume(
                    new ApplyStatusEffectsConsumeEffect(
                            new MobEffectInstance(MobEffects.REGENERATION, 300, 1)
                    )
            )
            .build();
}

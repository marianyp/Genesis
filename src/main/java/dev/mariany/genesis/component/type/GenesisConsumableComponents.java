package dev.mariany.genesis.component.type;

import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.item.consume.RemoveEffectsConsumeEffect;
import net.minecraft.sound.SoundEvents;

public class GenesisConsumableComponents {
    public static final ConsumableComponent HEALTHY_STEW = ConsumableComponents.food()
            .consumeEffect(
                    new ApplyEffectsConsumeEffect(
                            new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0)
                    )
            )
            .build();

    public static final ConsumableComponent ENCHANTED_HONEY_BOTTLE = ConsumableComponents.drink()
            .consumeSeconds(2F)
            .sound(SoundEvents.ITEM_HONEY_BOTTLE_DRINK)
            .consumeEffect(new RemoveEffectsConsumeEffect(StatusEffects.POISON))
            .consumeEffect(new ApplyEffectsConsumeEffect(
                            new StatusEffectInstance(StatusEffects.REGENERATION, 225, 1)
                    )
            )
            .build();
}

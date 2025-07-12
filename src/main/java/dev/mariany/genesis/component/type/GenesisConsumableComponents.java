package dev.mariany.genesis.component.type;

import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;

public class GenesisConsumableComponents {
    public static final ConsumableComponent HEALTHY_STEW = ConsumableComponents.food()
            .consumeEffect(
                    new ApplyEffectsConsumeEffect(
                            new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0)
                    )
            )
            .build();
}

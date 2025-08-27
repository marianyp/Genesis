package dev.mariany.genesis.event.entity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;

public class EntityEvents {
    private EntityEvents() {
    }

    public static final Event<BeforeEntityDeath> BEFORE_ENTITY_DEATH = EventFactory.createArrayBacked(
            BeforeEntityDeath.class,
            callbacks -> map -> {
                for (BeforeEntityDeath callback : callbacks) {
                    callback.onEntityDeath(map);
                }
            }
    );

    public interface BeforeEntityDeath {
        void onEntityDeath(LivingEntity livingEntity);
    }
}

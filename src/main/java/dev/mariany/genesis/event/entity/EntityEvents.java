package dev.mariany.genesis.event.entity;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class EntityEvents {
    private EntityEvents() {
    }

    public static final Event<BeforeEntityDeath> BEFORE_ENTITY_DEATH = EventFactory.createArrayBacked(
            BeforeEntityDeath.class,
            callbacks -> entity -> {
                for (BeforeEntityDeath callback : callbacks) {
                    callback.onEntityDeath(entity);
                }
            }
    );

    public static final Event<AfterEntityConsume> AFTER_ENTITY_CONSUME = EventFactory.createArrayBacked(
            AfterEntityConsume.class,
            callbacks -> (entity, stack) -> {
                for (AfterEntityConsume callback : callbacks) {
                    callback.onEntityConsume(entity, stack);
                }
            }
    );

    public interface BeforeEntityDeath {
        void onEntityDeath(LivingEntity livingEntity);
    }

    public interface AfterEntityConsume {
        void onEntityConsume(LivingEntity livingEntity, ItemStack stack);
    }
}

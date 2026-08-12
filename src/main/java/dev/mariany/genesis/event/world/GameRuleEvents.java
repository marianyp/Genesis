package dev.mariany.genesis.event.world;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public final class GameRuleEvents {
    public static final Event<ModifyBooleanDefault> MODIFY_BOOLEAN_DEFAULT = EventFactory.createArrayBacked(
            ModifyBooleanDefault.class,
            callbacks -> (name, defaultValue) -> {
                boolean modifiedDefault = defaultValue;

                for (ModifyBooleanDefault callback : callbacks) {
                    modifiedDefault = callback.modify(name, modifiedDefault);
                }

                return modifiedDefault;
            }
    );

    private GameRuleEvents() {
    }

    @FunctionalInterface
    public interface ModifyBooleanDefault {
        boolean modify(String name, boolean defaultValue);
    }
}

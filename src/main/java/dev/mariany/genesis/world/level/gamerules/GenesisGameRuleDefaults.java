package dev.mariany.genesis.world.level.gamerules;

import dev.mariany.genesis.event.world.GameRuleEvents;

import java.util.HashMap;
import java.util.Map;

public final class GenesisGameRuleDefaults {
    private static final Map<String, Boolean> BOOLEAN_OVERRIDES = new HashMap<>();

    static {
        registerBooleanOverride("natural_health_regeneration", false);
    }

    private GenesisGameRuleDefaults() {
    }

    public static void bootstrap() {
        GameRuleEvents.MODIFY_BOOLEAN_DEFAULT.register(GenesisGameRuleDefaults.BOOLEAN_OVERRIDES::getOrDefault);
    }

    public static void registerBooleanOverride(String name, boolean defaultValue) {
        GenesisGameRuleDefaults.BOOLEAN_OVERRIDES.put(name, defaultValue);
    }
}

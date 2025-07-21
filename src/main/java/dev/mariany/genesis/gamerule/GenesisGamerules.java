package dev.mariany.genesis.gamerule;

import dev.mariany.genesis.Genesis;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public class GenesisGamerules {
    public static final GameRules.Key<GameRules.BooleanRule> SHARED_AGE_PROGRESSION = GameRuleRegistry.register(
            "sharedAgeProgression", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));

    public static void bootstrap() {
        Genesis.LOGGER.info("Registering Gamerules for " + Genesis.MOD_ID);
    }
}

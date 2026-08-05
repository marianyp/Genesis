package dev.mariany.genesis.world.level.gamerules;

import dev.mariany.genesis.Genesis;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;

public class GenesisGameRules {
    public static final GameRule<Double> MINIMUM_DAYS_BEFORE_SLEEPING = GameRuleBuilder
            .forDouble(1)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(Genesis.id("minimum_days_before_sleeping"));

    public static final GameRule<Double> SOLACE_CAMPFIRE_RADIUS = GameRuleBuilder
            .forDouble(3.5)
            .category(GameRuleCategory.MISC)
            .buildAndRegister(Genesis.id("solace_campfire_radius"));

    public static final GameRule<Double> OCEAN_MONUMENT_RADIUS = GameRuleBuilder
            .forDouble(60)
            .category(GameRuleCategory.MISC)
            .buildAndRegister(Genesis.id("ocean_monument_radius"));

    public static final GameRule<Boolean> HIGHLIGHT_ELDER_GUARDIANS = GameRuleBuilder
            .forBoolean(true)
            .category(GameRuleCategory.MOBS)
            .buildAndRegister(Genesis.id("highlight_elder_guardians"));

    public static final GameRule<Boolean> RESPAWN_RESETS_TIREDNESS = GameRuleBuilder
            .forBoolean(false)
            .category(GameRuleCategory.PLAYER)
            .buildAndRegister(Genesis.id("respawn_resets_tiredness"));

    public static void bootstrap() {
        Genesis.bootstrapLog("Game Rules");
    }
}

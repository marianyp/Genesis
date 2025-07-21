package dev.mariany.genesis.gamerule;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.age.AgeShareManager;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;

import java.util.List;

public class GenesisGamerules {
    public static final GameRules.Key<GameRules.BooleanRule> SHARED_AGE_PROGRESSION = GameRuleRegistry.register(
            "sharedAgeProgression", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(
                    false,
                    (server, sharedAgeProgression) -> {
                        if (sharedAgeProgression.get()) {
                            AgeShareManager ageShareManager = AgeShareManager.getServerState(server);
                            List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();

                            Genesis.LOGGER.info("Preparing to apply shared ages to {} players", players.size());

                            for (ServerPlayerEntity player : players) {
                                ageShareManager.applySharedAges(player);
                            }
                        }
                    }
            )
    );

    public static void bootstrap() {
        Genesis.LOGGER.info("Registering Gamerules for " + Genesis.MOD_ID);
    }
}

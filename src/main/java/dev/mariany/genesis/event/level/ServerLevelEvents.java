package dev.mariany.genesis.event.level;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerLevel;

public final class ServerLevelEvents {
    public static final Event<BeforeAwakenPlayers> BEFORE_AWAKEN_PLAYERS = EventFactory.createArrayBacked(
            BeforeAwakenPlayers.class,
            callbacks -> level -> {
                for (BeforeAwakenPlayers callback : callbacks) {
                    callback.beforeWakeUpAllPlayers(level);
                }
            }
    );

    private ServerLevelEvents() {
    }

    @FunctionalInterface
    public interface BeforeAwakenPlayers {
        void beforeWakeUpAllPlayers(ServerLevel level);
    }
}

package dev.mariany.genesis.event.server.advancement;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.advancement.Advancement;
import net.minecraft.util.Identifier;

import java.util.Map;

public class ServerAdvancementEvents {
    private ServerAdvancementEvents() {
    }

    /**
     * Called when advancements are loaded or updated.
     */
    public static final Event<BeforeAdvancementsLoad> BEFORE_ADVANCEMENTS_LOAD = EventFactory.createArrayBacked(
            BeforeAdvancementsLoad.class,
            callbacks -> map -> {
                for (BeforeAdvancementsLoad callback : callbacks) {
                    callback.onAdvancementsLoaded(map);
                }
            });

    public interface BeforeAdvancementsLoad {
        /**
         * @param advancementMap Mutable map for advancements that will be loaded.
         */
        void onAdvancementsLoaded(Map<Identifier, Advancement> advancementMap);
    }
}

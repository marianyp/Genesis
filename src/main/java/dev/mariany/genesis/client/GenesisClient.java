package dev.mariany.genesis.client;

import dev.mariany.genesis.client.gui.screen.ingame.KilnScreen;
import dev.mariany.genesis.screen.GenesisScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class GenesisClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerScreenHandlers();
    }

    public static void registerScreenHandlers() {
        HandledScreens.register(GenesisScreenHandlers.KILN, KilnScreen::new);
    }
}

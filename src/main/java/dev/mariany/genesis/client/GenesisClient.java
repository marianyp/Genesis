package dev.mariany.genesis.client;

import dev.mariany.genesis.client.gui.screen.ingame.KilnScreen;
import dev.mariany.genesis.packet.clientbound.ClientboundPackets;
import dev.mariany.genesis.screen.GenesisScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

@Environment(EnvType.CLIENT)
public class GenesisClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerScreenHandlers();
        ClientboundPackets.init();
    }

    public static void registerScreenHandlers() {
        HandledScreens.register(GenesisScreenHandlers.KILN, KilnScreen::new);
    }
}

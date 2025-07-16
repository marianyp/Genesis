package dev.mariany.genesis.client;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.client.age.ClientAgeManager;
import dev.mariany.genesis.client.gui.screen.ingame.KilnScreen;
import dev.mariany.genesis.packet.clientbound.ClientboundPackets;
import dev.mariany.genesis.screen.GenesisScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.network.ClientPlayNetworkHandler;

@Environment(EnvType.CLIENT)
public class GenesisClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerScreenHandlers();
        ClientboundPackets.init();

        ClientPlayConnectionEvents.INIT.register(GenesisClient::cleanupAges);
        ClientPlayConnectionEvents.DISCONNECT.register(GenesisClient::cleanupAges);
    }

    private static void registerScreenHandlers() {
        HandledScreens.register(GenesisScreenHandlers.KILN, KilnScreen::new);
    }

    private static void cleanupAges(
            ClientPlayNetworkHandler clientPlayNetworkHandler,
            MinecraftClient minecraftClient
    ) {
        Genesis.LOGGER.info("Resetting Client Age Manager");
        ClientAgeManager.getInstance().reset();
    }
}

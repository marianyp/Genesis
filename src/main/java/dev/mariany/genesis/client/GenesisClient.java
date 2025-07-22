package dev.mariany.genesis.client;

import dev.mariany.genesis.client.age.ClientAgeManager;
import dev.mariany.genesis.client.gui.screen.ingame.KilnScreen;
import dev.mariany.genesis.client.instruction.ClientInstructionManager;
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

        ClientPlayConnectionEvents.INIT.register(GenesisClient::cleanup);
        ClientPlayConnectionEvents.DISCONNECT.register(GenesisClient::cleanup);
    }

    private static void registerScreenHandlers() {
        HandledScreens.register(GenesisScreenHandlers.KILN, KilnScreen::new);
    }

    private static void cleanup(
            ClientPlayNetworkHandler clientPlayNetworkHandler,
            MinecraftClient minecraftClient
    ) {
        ClientAgeManager.getInstance().reset();
        ClientInstructionManager.getInstance().reset();
    }
}

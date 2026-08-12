package dev.mariany.genesis.client;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.client.gui.screen.AssemblyRecipeBookHandler;
import dev.mariany.genesis.client.gui.screen.GenesisWidgets;
import dev.mariany.genesis.client.gui.screen.ingame.AssemblyScreen;
import dev.mariany.genesis.client.gui.screen.ingame.KilnScreen;
import dev.mariany.genesis.client.particle.SolaceParticle;
import dev.mariany.genesis.client.recipe.display.ClientRecipeDisplayRegistry;
import dev.mariany.genesis.client.render.entity.BoarEntityRenderer;
import dev.mariany.genesis.client.render.entity.model.GenesisModelLayers;
import dev.mariany.genesis.config.ConfigHandler;
import dev.mariany.genesis.config.GenesisClientConfig;
import dev.mariany.genesis.entity.GenesisEntityTypes;
import dev.mariany.genesis.packet.clientbound.ClientBoundPackets;
import dev.mariany.genesis.particle.GenesisParticleTypes;
import dev.mariany.genesis.screen.GenesisScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;

@Environment(EnvType.CLIENT)
public class GenesisClient implements ClientModInitializer {
    public static final ClientRecipeDisplayRegistry RECIPE_DISPLAY_REGISTRY = new ClientRecipeDisplayRegistry();

    private static final ConfigHandler<GenesisClientConfig> CONFIG_HANDLER = new ConfigHandler<>(
            Genesis.MOD_ID + "-client",
            new GenesisClientConfig()
    );

    public static GenesisClientConfig getConfig() {
        return CONFIG_HANDLER.getConfig();
    }

    @Override
    public void onInitializeClient() {
        CONFIG_HANDLER.loadConfig();

        ClientBoundPackets.register();
        GenesisModelLayers.bootstrap();
        GenesisWidgets.bootstrap();
        AssemblyRecipeBookHandler.bootstrap();

        registerEntityRenderer();
        registerScreenHandlers();
        registerParticles();

        RECIPE_DISPLAY_REGISTRY.bootstrap();
    }

    private static void registerEntityRenderer() {
        EntityRenderers.register(GenesisEntityTypes.BOAR, BoarEntityRenderer::new);
    }

    private static void registerScreenHandlers() {
        MenuScreens.register(GenesisScreenHandlers.KILN, KilnScreen::new);
        MenuScreens.register(GenesisScreenHandlers.ASSEMBLY, AssemblyScreen::new);
    }

    private static void registerParticles() {
        ParticleProviderRegistry.getInstance().register(GenesisParticleTypes.SOLACE, SolaceParticle.Provider::new);
    }
}

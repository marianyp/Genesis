package dev.mariany.genesis.screen;

import dev.mariany.genesis.Genesis;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class GenesisScreenHandlers {
    public static final ScreenHandlerType<KilnScreenHandler> KILN = register("kiln", KilnScreenHandler::new);

    private static <T extends ScreenHandler> ScreenHandlerType<T> register(String id, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, Genesis.id(id), new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
    }

    public static void bootstrap() {
        Genesis.LOGGER.info("Registering Screen Handlers for " + Genesis.MOD_ID);
    }
}

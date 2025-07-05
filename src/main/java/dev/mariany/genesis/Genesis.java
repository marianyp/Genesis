package dev.mariany.genesis;

import dev.mariany.genesis.item.GenesisItems;
import dev.mariany.genesis.sound.GenesisSoundEvents;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Genesis implements ModInitializer {
    public static final String MOD_ID = "genesis";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        GenesisSoundEvents.bootstrap();
        GenesisItems.bootstrap();
    }

    public static Identifier id(String resource) {
        return Identifier.of(MOD_ID, resource);
    }
}
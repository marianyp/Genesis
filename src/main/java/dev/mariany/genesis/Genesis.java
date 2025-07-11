package dev.mariany.genesis;

import dev.mariany.genesis.advancement.criterion.GenesisCriteria;
import dev.mariany.genesis.block.GenesisBlocks;
import dev.mariany.genesis.block.custom.cauldron.PrimitiveCauldronBehavior;
import dev.mariany.genesis.block.entity.GenesisBlockEntities;
import dev.mariany.genesis.event.block.UseBlockHandler;
import dev.mariany.genesis.event.server.BeforeAdvancementsLoadHandler;
import dev.mariany.genesis.event.server.advancement.ServerAdvancementEvents;
import dev.mariany.genesis.item.GenesisItems;
import dev.mariany.genesis.screen.GenesisScreenHandlers;
import dev.mariany.genesis.sound.GenesisSoundEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
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
        GenesisScreenHandlers.bootstrap();
        GenesisBlocks.bootstrap();
        GenesisBlockEntities.bootstrap();
        PrimitiveCauldronBehavior.registerBehavior();
        GenesisCriteria.bootstrap();

        UseBlockCallback.EVENT.register(UseBlockHandler::onUseBlock);

        ServerAdvancementEvents.BEFORE_ADVANCEMENTS_LOAD.register(BeforeAdvancementsLoadHandler::onAdvancementsLoad);
    }

    public static Identifier id(String resource) {
        return Identifier.of(MOD_ID, resource);
    }
}
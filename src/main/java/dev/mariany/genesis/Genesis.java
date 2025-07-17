package dev.mariany.genesis;

import dev.mariany.genesis.advancement.criterion.GenesisCriteria;
import dev.mariany.genesis.block.GenesisBlocks;
import dev.mariany.genesis.block.custom.cauldron.PrimitiveCauldronBehavior;
import dev.mariany.genesis.block.entity.GenesisBlockEntities;
import dev.mariany.genesis.event.block.UseBlockHandler;
import dev.mariany.genesis.event.item.ModifyItemComponentsHandler;
import dev.mariany.genesis.event.server.SyncDataPackContentsHandler;
import dev.mariany.genesis.event.server.advancement.BeforeAdvancementsLoadHandler;
import dev.mariany.genesis.event.server.advancement.ServerAdvancementEvents;
import dev.mariany.genesis.item.GenesisItems;
import dev.mariany.genesis.loot.LootTableModifiers;
import dev.mariany.genesis.packet.GenesisPackets;
import dev.mariany.genesis.screen.GenesisScreenHandlers;
import dev.mariany.genesis.sound.GenesisSoundEvents;
import dev.mariany.genesis.stat.GenesisStats;
import dev.mariany.genesis.village.GenesisTradeOffers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Genesis implements ModInitializer {
    public static final String MOD_ID = "genesis";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        GenesisPackets.register();
        GenesisSoundEvents.bootstrap();
        GenesisItems.bootstrap();
        GenesisScreenHandlers.bootstrap();
        GenesisBlocks.bootstrap();
        GenesisBlockEntities.bootstrap();
        PrimitiveCauldronBehavior.registerBehavior();
        GenesisCriteria.bootstrap();
        GenesisStats.bootstrap();
        LootTableModifiers.modifyLootTables();

        DefaultItemComponentEvents.MODIFY.register(ModifyItemComponentsHandler::modify);
        UseBlockCallback.EVENT.register(UseBlockHandler::onUseBlock);
        ServerAdvancementEvents.BEFORE_ADVANCEMENTS_LOAD.register(BeforeAdvancementsLoadHandler::beforeAdvancementsLoad);
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(SyncDataPackContentsHandler::onSyncDataPackContents);

        GenesisTradeOffers.registerVillagerOffers();
    }

    public static Identifier id(String resource) {
        return Identifier.of(MOD_ID, resource);
    }
}
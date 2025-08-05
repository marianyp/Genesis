package dev.mariany.genesis;

import dev.mariany.genesis.advancement.criterion.GenesisCriteria;
import dev.mariany.genesis.block.GenesisBlocks;
import dev.mariany.genesis.block.custom.cauldron.PrimitiveCauldronBehavior;
import dev.mariany.genesis.block.entity.GenesisBlockEntities;
import dev.mariany.genesis.config.ConfigHandler;
import dev.mariany.genesis.entity.GenesisEntities;
import dev.mariany.genesis.event.item.ModifyItemComponentsHandler;
import dev.mariany.genesis.item.GenesisItems;
import dev.mariany.genesis.loot.LootTableModifiers;
import dev.mariany.genesis.recipe.GenesisRecipeTypes;
import dev.mariany.genesis.recipe.brew.GenesisBrewingRecipes;
import dev.mariany.genesis.recipe.display.AssemblyCraftingRecipeDisplay;
import dev.mariany.genesis.screen.GenesisScreenHandlers;
import dev.mariany.genesis.sound.GenesisSoundEvents;
import dev.mariany.genesis.stat.GenesisStats;
import dev.mariany.genesis.village.GenesisTradeOffers;
import dev.mariany.genesis.world.gen.GenesisEntitySpawns;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Genesis implements ModInitializer {
    public static final String MOD_ID = "genesis";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String resource) {
        return Identifier.of(MOD_ID, resource);
    }

    @Override
    public void onInitialize() {
        ConfigHandler.loadConfig();

        GenesisSoundEvents.bootstrap();
        GenesisItems.bootstrap();
        GenesisBrewingRecipes.registerBrewingRecipes();
        GenesisScreenHandlers.bootstrap();
        GenesisBlocks.bootstrap();
        GenesisBlockEntities.bootstrap();
        GenesisEntities.bootstrap();
        GenesisEntitySpawns.addSpawns();
        PrimitiveCauldronBehavior.registerBehavior();
        GenesisCriteria.bootstrap();
        GenesisStats.bootstrap();
        LootTableModifiers.modifyLootTables();

        DefaultItemComponentEvents.MODIFY.register(ModifyItemComponentsHandler::modify);

        GenesisTradeOffers.registerVillagerOffers();

        GenesisRecipeTypes.bootstrap();
        bootstrapRecipeDisplay();
    }

    private static void bootstrapRecipeDisplay() {
        Registry.register(Registries.RECIPE_DISPLAY, id("assembly"), AssemblyCraftingRecipeDisplay.SERIALIZER);
    }
}
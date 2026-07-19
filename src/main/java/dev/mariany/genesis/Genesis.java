package dev.mariany.genesis;

import dev.mariany.genesis.advancement.criterion.GenesisCriteria;
import dev.mariany.genesis.attachment.GenesisAttachmentTypes;
import dev.mariany.genesis.block.GenesisBlocks;
import dev.mariany.genesis.block.custom.cauldron.PrimitiveCauldronBehavior;
import dev.mariany.genesis.block.entity.GenesisBlockEntities;
import dev.mariany.genesis.entity.GenesisEntityTypes;
import dev.mariany.genesis.item.GenesisItems;
import dev.mariany.genesis.logic.OceanMonumentLogic;
import dev.mariany.genesis.logic.SalmonellaLogic;
import dev.mariany.genesis.logic.SolaceLogic;
import dev.mariany.genesis.logic.TirednessLogic;
import dev.mariany.genesis.loot.LootTableModifiers;
import dev.mariany.genesis.packet.GenesisPackets;
import dev.mariany.genesis.particle.GenesisParticleTypes;
import dev.mariany.genesis.recipe.GenesisRecipeTypes;
import dev.mariany.genesis.recipe.brew.GenesisBrewingRecipes;
import dev.mariany.genesis.recipe.display.AssemblyCraftingRecipeDisplay;
import dev.mariany.genesis.screen.GenesisScreenHandlers;
import dev.mariany.genesis.sound.GenesisSoundEvents;
import dev.mariany.genesis.stat.GenesisStats;
import dev.mariany.genesis.world.effect.GenesisMobEffects;
import dev.mariany.genesis.world.gen.GenesisEntitySpawns;
import dev.mariany.genesis.world.level.gamerules.GenesisGameRules;
import dev.mariany.genesis.world.level.storage.loot.functions.GenesisLootItemFunctions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Genesis implements ModInitializer {
    public static final String MOD_ID = "genesis";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final TirednessLogic TIREDNESS_LOGIC = new TirednessLogic();
    public static final SolaceLogic SOLACE_LOGIC = new SolaceLogic();

    public static Identifier id(String resource) {
        return Identifier.fromNamespaceAndPath(MOD_ID, resource);
    }

    public static void bootstrapLog(String type) {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
            return;
        }

        LOGGER.info("Registering {}", type);
    }

    @Override
    public void onInitialize() {
        GenesisPackets.bootstrap();
        GenesisAttachmentTypes.bootstrap();
        GenesisSoundEvents.bootstrap();
        GenesisItems.bootstrap();
        GenesisBrewingRecipes.bootstrap();
        GenesisScreenHandlers.bootstrap();
        GenesisBlocks.bootstrap();
        GenesisBlockEntities.bootstrap();
        GenesisEntityTypes.bootstrap();
        GenesisEntitySpawns.bootstrap();
        PrimitiveCauldronBehavior.bootstrap();
        GenesisCriteria.bootstrap();
        GenesisStats.bootstrap();
        LootTableModifiers.bootstrap();
        GenesisParticleTypes.bootstrap();
        GenesisLootItemFunctions.bootstrap();
        OceanMonumentLogic.bootstrap();
        GenesisMobEffects.bootstrap();
        Genesis.SOLACE_LOGIC.bootstrap();
        SalmonellaLogic.bootstrap();
        GenesisGameRules.bootstrap();
        Genesis.TIREDNESS_LOGIC.bootstrap();
        GenesisRecipeTypes.bootstrap();
        bootstrapRecipeDisplay();
    }

    private static void bootstrapRecipeDisplay() {
        Registry.register(
                BuiltInRegistries.RECIPE_DISPLAY,
                id("assembly"),
                AssemblyCraftingRecipeDisplay.SERIALIZER
        );
    }
}
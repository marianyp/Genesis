package dev.mariany.genesis.datagen;

import dev.mariany.genesis.world.item.trading.GenesisVillagerTrades;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.VillagerTradeTags;
import net.minecraft.world.item.trading.VillagerTrade;

import java.util.concurrent.CompletableFuture;

public class GenesisVillagerTradesTagsProvider extends FabricTagsProvider<VillagerTrade> {
    public GenesisVillagerTradesTagsProvider(
            FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registryLookupFuture
    ) {
        super(output, Registries.VILLAGER_TRADE, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(VillagerTradeTags.CARTOGRAPHER_LEVEL_3)
                .add(GenesisVillagerTrades.CARTOGRAPHER_3_EMERALD_AND_COMPASS_DEEP_DARK);
    }
}

package dev.mariany.genesis.village;

import dev.mariany.genesis.tag.GenesisTags;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.map.MapDecorationTypes;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;

public class GenesisTradeOffers {
    public static void registerVillagerOffers() {
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.CARTOGRAPHER, 2, factories -> {
            factories.add(
                    new TradeOffers.SellMapFactory(14,
                            GenesisTags.Structures.ON_DEEP_DARK_EXPLORER_MAPS,
                            "filled_map.ancient_city",
                            MapDecorationTypes.RED_X,
                            12,
                            10
                    )
            );

            factories.add(
                    new SellBiomeMapFactory(14,
                            GenesisTags.Biomes.ON_PALE_GARDEN_EXPLORER_MAPS,
                            "filled_map.pale_garden",
                            MapDecorationTypes.RED_X,
                            12,
                            10
                    )
            );
        });
    }
}

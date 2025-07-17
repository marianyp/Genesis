package dev.mariany.genesis.village;

import com.mojang.datafixers.util.Pair;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.item.map.MapState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SellBiomeMapFactory implements TradeOffers.Factory {
    private final int price;
    private final TagKey<Biome> biome;
    private final String nameKey;
    private final RegistryEntry<MapDecorationType> decoration;
    private final int maxUses;
    private final int experience;

    public SellBiomeMapFactory(int price, TagKey<Biome> biome, String nameKey, RegistryEntry<MapDecorationType> decoration, int maxUses, int experience) {
        this.price = price;
        this.biome = biome;
        this.nameKey = nameKey;
        this.decoration = decoration;
        this.maxUses = maxUses;
        this.experience = experience;
    }

    @Nullable
    @Override
    public TradeOffer create(Entity entity, Random random) {
        if (entity.getWorld() instanceof ServerWorld serverWorld) {
            Pair<BlockPos, RegistryEntry<Biome>> results = serverWorld.locateBiome(
                    biome -> biome.isIn(this.biome),
                    entity.getBlockPos(),
                    6400,
                    32,
                    64
            );

            if (results != null) {
                BlockPos blockPos = results.getFirst();
                ItemStack itemStack = FilledMapItem.createMap(
                        serverWorld,
                        blockPos.getX(),
                        blockPos.getZ(),
                        (byte) 2,
                        true,
                        true
                );
                FilledMapItem.fillExplorationMap(serverWorld, itemStack);
                MapState.addDecorationsNbt(itemStack, blockPos, "+", this.decoration);
                itemStack.set(DataComponentTypes.ITEM_NAME, Text.translatable(this.nameKey));

                return new TradeOffer(
                        new TradedItem(Items.EMERALD, this.price),
                        Optional.of(new TradedItem(Items.COMPASS)),
                        itemStack,
                        this.maxUses,
                        this.experience,
                        0.2F
                );
            }
        }

        return null;
    }
}

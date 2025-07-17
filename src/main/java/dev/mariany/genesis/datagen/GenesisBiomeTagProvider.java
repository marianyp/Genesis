package dev.mariany.genesis.datagen;

import dev.mariany.genesis.tag.GenesisTags;
import net.minecraft.data.DataOutput;
import net.minecraft.data.tag.SimpleTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import java.util.concurrent.CompletableFuture;

public class GenesisBiomeTagProvider extends SimpleTagProvider<Biome> {
    public GenesisBiomeTagProvider(
            DataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture
    ) {
        super(output, RegistryKeys.BIOME, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        this.builder(GenesisTags.Biomes.ON_PALE_GARDEN_EXPLORER_MAPS).add(BiomeKeys.PALE_GARDEN);
    }
}

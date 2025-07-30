package dev.mariany.genesis.datagen;

import dev.mariany.genesis.entity.GenesisEntities;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EntityTypeTags;

import java.util.concurrent.CompletableFuture;

public class GenesisEntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {
    public GenesisEntityTypeTagProvider(
            FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture
    ) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        this.valueLookupBuilder(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS).add(GenesisEntities.BOAR);
    }
}

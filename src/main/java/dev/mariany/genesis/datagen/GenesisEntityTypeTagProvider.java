package dev.mariany.genesis.datagen;

import dev.mariany.genesis.Genesis;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EntityTypeTags;

import java.util.concurrent.CompletableFuture;

public class GenesisEntityTypeTagProvider extends FabricTagsProvider.EntityTypeTagsProvider {
    public GenesisEntityTypeTagProvider(
            FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        this.tag(EntityTypeTags.POWDER_SNOW_WALKABLE_MOBS)
            .add(ResourceKey.create(Registries.ENTITY_TYPE, Genesis.id("boar")));
    }
}

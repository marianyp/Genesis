package dev.mariany.genesis.datagen;

import dev.mariany.genesis.tag.GenesisTags;
import net.minecraft.data.DataOutput;
import net.minecraft.data.tag.SimpleTagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;

import java.util.concurrent.CompletableFuture;

public class GenesisStructureTagProvider extends SimpleTagProvider<Structure> {
    public GenesisStructureTagProvider(
            DataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture
    ) {
        super(output, RegistryKeys.STRUCTURE, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        this.builder(GenesisTags.Structures.ON_DEEP_DARK_EXPLORER_MAPS).add(StructureKeys.ANCIENT_CITY);
    }
}

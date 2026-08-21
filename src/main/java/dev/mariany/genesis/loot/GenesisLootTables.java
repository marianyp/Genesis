package dev.mariany.genesis.loot;

import dev.mariany.genesis.Genesis;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public class GenesisLootTables {
    public static final ResourceKey<LootTable> SIFTING_DIRT = register("sifting/dirt");
    public static final ResourceKey<LootTable> SIFTING_GRAVEL = register("sifting/gravel");
    public static final ResourceKey<LootTable> SIFTING_SOUL = register("sifting/soul");

    private static ResourceKey<LootTable> register(String id) {
        return ResourceKey.create(Registries.LOOT_TABLE, Genesis.id(id));
    }
}

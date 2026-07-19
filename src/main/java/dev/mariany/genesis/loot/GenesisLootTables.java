package dev.mariany.genesis.loot;

import dev.mariany.genesis.Genesis;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public class GenesisLootTables {
    public static final ResourceKey<LootTable> DIRT_DUSTING = register("dusting/dirt");
    public static final ResourceKey<LootTable> GRAVEL_DUSTING = register("dusting/gravel");
    public static final ResourceKey<LootTable> SOUL_SEDIMENT_DUSTING = register("dusting/soul_sediment");

    private static ResourceKey<LootTable> register(String id) {
        return ResourceKey.create(Registries.LOOT_TABLE, Genesis.id(id));
    }
}

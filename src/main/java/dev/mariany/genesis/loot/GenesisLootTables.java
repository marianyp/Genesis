package dev.mariany.genesis.loot;

import dev.mariany.genesis.Genesis;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class GenesisLootTables {
    public static final RegistryKey<LootTable> DIRT_DUSTING = register("dusting/dirt");
    public static final RegistryKey<LootTable> GRAVEL_DUSTING = register("dusting/gravel");

    private static RegistryKey<LootTable> register(String id) {
        return RegistryKey.of(RegistryKeys.LOOT_TABLE, Genesis.id(id));
    }
}

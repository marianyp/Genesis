package dev.mariany.genesis.loot;

import dev.mariany.genesis.Genesis;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class GenesisLootTables {
    public static final RegistryKey<LootTable> CLAY_SIFTING = register("archaeology/clay_sifting");
    public static final RegistryKey<LootTable> FLINT_SIFTING = register("archaeology/flint_sifting");

    private static RegistryKey<LootTable> register(String id) {
        return RegistryKey.of(RegistryKeys.LOOT_TABLE, Genesis.id(id));
    }
}

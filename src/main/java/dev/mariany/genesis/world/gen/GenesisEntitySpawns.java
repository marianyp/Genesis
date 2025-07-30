package dev.mariany.genesis.world.gen;

import dev.mariany.genesis.entity.GenesisEntities;
import dev.mariany.genesis.entity.custom.mob.BoarEntity;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnLocationTypes;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.world.Heightmap;

public class GenesisEntitySpawns {
    public static void addSpawns() {
        BiomeModifications.addSpawn(
                BiomeSelectors.foundInOverworld(),
                SpawnGroup.MONSTER,
                GenesisEntities.BOAR,
                25,
                2,
                4
        );

        SpawnRestriction.register(
                GenesisEntities.BOAR,
                SpawnLocationTypes.ON_GROUND,
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                BoarEntity::canSpawn
        );
    }
}

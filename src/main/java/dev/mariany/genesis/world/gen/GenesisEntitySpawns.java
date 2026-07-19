package dev.mariany.genesis.world.gen;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.entity.GenesisEntityTypes;
import dev.mariany.genesis.entity.custom.mob.BoarEntity;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.levelgen.Heightmap;

public class GenesisEntitySpawns {
    private GenesisEntitySpawns() {
    }

    public static void bootstrap() {
        Genesis.bootstrapLog("Entity Spawns");

        BiomeModifications.addSpawn(
                BiomeSelectors.spawnsOneOf(EntityTypes.SPIDER),
                MobCategory.MONSTER,
                GenesisEntityTypes.BOAR,
                40,
                3,
                4
        );

        SpawnPlacements.register(
                GenesisEntityTypes.BOAR,
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                BoarEntity::canSpawn
        );
    }
}

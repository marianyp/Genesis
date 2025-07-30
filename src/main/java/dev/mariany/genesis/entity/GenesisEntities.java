package dev.mariany.genesis.entity;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.entity.custom.mob.BoarEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class GenesisEntities {
    public static final EntityType<BoarEntity> BOAR = register(
            "boar",
            EntityType.Builder.create(BoarEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.9F, 0.9F)
                    .maxTrackingRange(8)
    );

    private static <T extends Entity> EntityType<T> register(RegistryKey<EntityType<?>> key, EntityType.Builder<T> type) {
        return Registry.register(Registries.ENTITY_TYPE, key, type.build(key));
    }

    private static RegistryKey<EntityType<?>> keyOf(String id) {
        return RegistryKey.of(RegistryKeys.ENTITY_TYPE, Genesis.id(id));
    }

    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
        return register(keyOf(id), type);
    }

    public static void bootstrap() {
        FabricDefaultAttributeRegistry.register(BOAR, BoarEntity.createBoarAttributes());
    }
}

package dev.mariany.genesis.entity;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.entity.custom.mob.BoarEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class GenesisEntityTypes {
    public static final EntityType<BoarEntity> BOAR = register(
            "boar",
            EntityType.Builder.of(BoarEntity::new, MobCategory.MONSTER)
                              .sized(0.9F, 0.9F)
                              .clientTrackingRange(8)
                              .notInPeaceful()
    );

    private static <T extends Entity> EntityType<T> register(
            ResourceKey<EntityType<?>> key,
            EntityType.Builder<T> type
    ) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, type.build(key));
    }

    private static ResourceKey<EntityType<?>> keyOf(String id) {
        return ResourceKey.create(Registries.ENTITY_TYPE, Genesis.id(id));
    }

    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
        return register(keyOf(id), type);
    }

    public static void bootstrap() {
        Genesis.bootstrapLog("Entity Types");
        FabricDefaultAttributeRegistry.register(BOAR, BoarEntity.createAttributes());
    }
}

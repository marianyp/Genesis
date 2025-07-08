package dev.mariany.genesis.block.entity;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.block.GenesisBlocks;
import dev.mariany.genesis.block.entity.custom.FilledPrimitiveCauldronBlockEntity;
import dev.mariany.genesis.block.entity.custom.KilnBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class GenesisBlockEntities {
    public static final BlockEntityType<KilnBlockEntity> KILN = register("kiln",
            FabricBlockEntityTypeBuilder.create(KilnBlockEntity::new, GenesisBlocks.KILN).build());

    public static final BlockEntityType<FilledPrimitiveCauldronBlockEntity> FILLED_PRIMITIVE_CAULDRON = register("filled_primitive_cauldron",
            FabricBlockEntityTypeBuilder.create(
                    FilledPrimitiveCauldronBlockEntity::new,
                    GenesisBlocks.DIRT_TERRACOTTA_CAULDRON,
                    GenesisBlocks.GRAVEL_TERRACOTTA_CAULDRON
            ).build()
    );

    public static <T extends BlockEntityType<?>> T register(String path, T blockEntityType) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Genesis.id(path), blockEntityType);
    }

    public static void bootstrap() {
        Genesis.LOGGER.info("Registering Block Entities for " + Genesis.MOD_ID);
    }
}

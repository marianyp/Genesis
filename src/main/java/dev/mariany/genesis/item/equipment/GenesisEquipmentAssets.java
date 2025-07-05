package dev.mariany.genesis.item.equipment;

import dev.mariany.genesis.Genesis;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.registry.RegistryKey;

import java.util.function.BiConsumer;

public interface GenesisEquipmentAssets {
    RegistryKey<EquipmentAsset> COPPER = id("copper");

    static RegistryKey<EquipmentAsset> id(String name) {
        return RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, Genesis.id(name));
    }

    static void bootstrap(BiConsumer<RegistryKey<EquipmentAsset>, EquipmentModel> consumer) {
        consumer.accept(COPPER, onlyHumanoid("copper"));
    }

    static EquipmentModel onlyHumanoid(String name) {
        return EquipmentModel.builder().addHumanoidLayers(Genesis.id(name)).build();
    }
}

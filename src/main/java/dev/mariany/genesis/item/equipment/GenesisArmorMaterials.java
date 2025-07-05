package dev.mariany.genesis.item.equipment;

import dev.mariany.genesis.sound.GenesisSoundEvents;
import dev.mariany.genesis.tag.GenesisTags;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentType;

import java.util.Map;

public class GenesisArmorMaterials {
    public static final ArmorMaterial COPPER = new ArmorMaterial(
            10,
            Map.of(
                    EquipmentType.HELMET, 2,
                    EquipmentType.CHESTPLATE, 4,
                    EquipmentType.LEGGINGS, 3,
                    EquipmentType.BOOTS, 1
            ),
            5,
            GenesisSoundEvents.ITEM_ARMOR_EQUIP_COPPER,
            0F,
            0F,
            GenesisTags.Items.REPAIRS_COPPER_ARMOR,
            GenesisEquipmentAssets.COPPER
    );
}

package dev.mariany.genesis.item;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.item.custom.FlintsItem;
import dev.mariany.genesis.item.equipment.GenesisArmorMaterials;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import java.util.function.Function;

public class GenesisItems {
    private static final Item.Settings CAST_SETTINGS = new Item.Settings().maxCount(1);

    public static final Item COPPER_NUGGET = register("copper_nugget");

    public static final Item COPPER_SWORD = register("copper_sword", (new Item.Settings()).sword(GenesisToolMaterials.COPPER, 3.0F, -2.4F));
    public static final Item COPPER_SHOVEL = register("copper_shovel", ((settings) -> new ShovelItem(GenesisToolMaterials.COPPER, 1.5F, -3.0F, settings)));
    public static final Item COPPER_PICKAXE = register("copper_pickaxe", (new Item.Settings()).pickaxe(GenesisToolMaterials.COPPER, 1.0F, -2.8F));
    public static final Item COPPER_AXE = register("copper_axe", ((settings) -> new AxeItem(GenesisToolMaterials.COPPER, 6.0F, -3.1F, settings)));
    public static final Item COPPER_HOE = register("copper_hoe", ((settings) -> new HoeItem(GenesisToolMaterials.COPPER, -2.0F, -1.0F, settings)));

    public static final Item COPPER_HELMET = register("copper_helmet", (new Item.Settings()).armor(GenesisArmorMaterials.COPPER, EquipmentType.HELMET));
    public static final Item COPPER_CHESTPLATE = register("copper_chestplate", (new Item.Settings()).armor(GenesisArmorMaterials.COPPER, EquipmentType.CHESTPLATE));
    public static final Item COPPER_LEGGINGS = register("copper_leggings", (new Item.Settings()).armor(GenesisArmorMaterials.COPPER, EquipmentType.LEGGINGS));
    public static final Item COPPER_BOOTS = register("copper_boots", (new Item.Settings()).armor(GenesisArmorMaterials.COPPER, EquipmentType.BOOTS));

    public static final Item FLINTS = register("flints", FlintsItem::new, (new Item.Settings()).maxDamage(4));

    public static final Item RAW_COAL = register("raw_coal");
    public static final Item RAW_DIAMOND = register("raw_diamond");
    public static final Item RAW_REDSTONE = register("raw_redstone");
    public static final Item RAW_EMERALD = register("raw_emerald");
    public static final Item RAW_LAPIS_LAZULI = register("raw_lapis_lazuli");
    public static final Item RAW_NETHERITE = register("raw_netherite");

    public static final Item BLANK_CLAY_CAST = register("blank_clay_cast");
    public static final Item SWORD_CLAY_CAST = register("sword_clay_cast", CAST_SETTINGS);
    public static final Item SHOVEL_CLAY_CAST = register("shovel_clay_cast", CAST_SETTINGS);
    public static final Item PICKAXE_CLAY_CAST = register("pickaxe_clay_cast", CAST_SETTINGS);
    public static final Item AXE_CLAY_CAST = register("axe_clay_cast", CAST_SETTINGS);
    public static final Item HOE_CLAY_CAST = register("hoe_clay_cast", CAST_SETTINGS);
    public static final Item SHIELD_CLAY_CAST = register("shield_clay_cast", CAST_SETTINGS);
    public static final Item ANVIL_CLAY_CAST = register("anvil_clay_cast", CAST_SETTINGS);
    public static final Item TOTEM_CLAY_CAST = register("totem_clay_cast", CAST_SETTINGS);

    public static final Item SWORD_CAST = register("sword_cast", CAST_SETTINGS);
    public static final Item SHOVEL_CAST = register("shovel_cast", CAST_SETTINGS);
    public static final Item PICKAXE_CAST = register("pickaxe_cast", CAST_SETTINGS);
    public static final Item AXE_CAST = register("axe_cast", CAST_SETTINGS);
    public static final Item HOE_CAST = register("hoe_cast", CAST_SETTINGS);
    public static final Item SHIELD_CAST = register("shield_cast", CAST_SETTINGS);
    public static final Item ANVIL_CAST = register("anvil_cast", CAST_SETTINGS);
    public static final Item TOTEM_CAST = register("totem_cast", CAST_SETTINGS);

    private static RegistryKey<Item> keyOf(String id) {
        return RegistryKey.of(RegistryKeys.ITEM, Genesis.id(id));
    }

    private static Item register(String name) {
        return register(name, Item::new, new Item.Settings());
    }

    private static Item register(String name, Item.Settings settings) {
        return register(name, Item::new, settings);
    }

    private static Item register(String name, Function<Item.Settings, Item> factory) {
        return register(name, factory, new Item.Settings());
    }

    private static Item register(String name, Function<Item.Settings, Item> factory, Item.Settings settings) {
        RegistryKey<Item> itemKey = keyOf(name);
        Item item = factory.apply(settings.registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }

    public static void bootstrap() {
        Genesis.LOGGER.info("Registering items for " + Genesis.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.addAfter(Items.IRON_NUGGET, COPPER_NUGGET);

            entries.addAfter(Items.RAW_GOLD, RAW_NETHERITE);
            entries.addAfter(RAW_NETHERITE, RAW_COAL);
            entries.addAfter(RAW_COAL, RAW_EMERALD);
            entries.addAfter(RAW_EMERALD, RAW_LAPIS_LAZULI);
            entries.addAfter(RAW_LAPIS_LAZULI, RAW_DIAMOND);
            entries.addAfter(RAW_DIAMOND, RAW_REDSTONE);

            entries.addBefore(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, BLANK_CLAY_CAST);
            entries.addAfter(BLANK_CLAY_CAST, SWORD_CLAY_CAST);
            entries.addAfter(SWORD_CLAY_CAST, SHOVEL_CLAY_CAST);
            entries.addAfter(SHOVEL_CLAY_CAST, PICKAXE_CLAY_CAST);
            entries.addAfter(PICKAXE_CLAY_CAST, AXE_CLAY_CAST);
            entries.addAfter(AXE_CLAY_CAST, HOE_CLAY_CAST);
            entries.addAfter(HOE_CLAY_CAST, SHIELD_CLAY_CAST);
            entries.addAfter(SHIELD_CLAY_CAST, ANVIL_CLAY_CAST);
            entries.addAfter(ANVIL_CLAY_CAST, TOTEM_CLAY_CAST);

            entries.addAfter(TOTEM_CLAY_CAST, SWORD_CAST);
            entries.addAfter(SWORD_CAST, SHOVEL_CAST);
            entries.addAfter(SHOVEL_CAST, PICKAXE_CAST);
            entries.addAfter(PICKAXE_CAST, AXE_CAST);
            entries.addAfter(AXE_CAST, HOE_CAST);
            entries.addAfter(HOE_CAST, SHIELD_CAST);
            entries.addAfter(SHIELD_CAST, ANVIL_CAST);
            entries.addAfter(ANVIL_CAST, TOTEM_CAST);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.addAfter(Items.IRON_SWORD, COPPER_SWORD);
            entries.addAfter(Items.IRON_AXE, COPPER_AXE);

            entries.addAfter(Items.IRON_BOOTS, COPPER_HELMET);
            entries.addAfter(COPPER_HELMET, COPPER_CHESTPLATE);
            entries.addAfter(COPPER_CHESTPLATE, COPPER_LEGGINGS);
            entries.addAfter(COPPER_LEGGINGS, COPPER_BOOTS);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.addAfter(Items.IRON_HOE, COPPER_SHOVEL);
            entries.addAfter(COPPER_SHOVEL, COPPER_PICKAXE);
            entries.addAfter(COPPER_PICKAXE, COPPER_AXE);
            entries.addAfter(COPPER_AXE, COPPER_HOE);

            entries.addAfter(Items.FLINT_AND_STEEL, FLINTS);
        });
    }
}

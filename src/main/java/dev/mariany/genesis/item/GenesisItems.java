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

package dev.mariany.genesis.item;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.GenesisConstants;
import dev.mariany.genesis.component.type.GenesisConsumableComponents;
import dev.mariany.genesis.component.type.GenesisFoodComponents;
import dev.mariany.genesis.entity.GenesisEntities;
import dev.mariany.genesis.item.custom.AssemblyPatternItem;
import dev.mariany.genesis.item.custom.FlintsItem;
import dev.mariany.genesis.item.equipment.GenesisArmorMaterials;
import dev.mariany.genesis.recipe.CraftingPattern;
import dev.mariany.genesis.tag.GenesisTags;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.item.*;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Rarity;

import java.util.function.Function;

public class GenesisItems {
    private static final Item.Settings CAST_SETTINGS = new Item.Settings().maxCount(1);

    public static final Item COPPER_NUGGET = register("copper_nugget");

    public static final Item COPPER_SWORD = register("copper_sword", (new Item.Settings()).sword(GenesisToolMaterials.COPPER, 3F, -2.4F));
    public static final Item COPPER_SHOVEL = register("copper_shovel", ((settings) -> new ShovelItem(GenesisToolMaterials.COPPER, 1.5F, -3F, settings)));
    public static final Item COPPER_PICKAXE = register("copper_pickaxe", (new Item.Settings()).pickaxe(GenesisToolMaterials.COPPER, 1F, -2.8F));
    public static final Item COPPER_AXE = register("copper_axe", ((settings) -> new AxeItem(GenesisToolMaterials.COPPER, 7F, -3.2F, settings)));
    public static final Item COPPER_HOE = register("copper_hoe", ((settings) -> new HoeItem(GenesisToolMaterials.COPPER, -1F, -2F, settings)));

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
    public static final Item CLAY_SWORD_CAST = register("clay_sword_cast", CAST_SETTINGS);
    public static final Item CLAY_SHOVEL_CAST = register("clay_shovel_cast", CAST_SETTINGS);
    public static final Item CLAY_PICKAXE_CAST = register("clay_pickaxe_cast", CAST_SETTINGS);
    public static final Item CLAY_AXE_CAST = register("clay_axe_cast", CAST_SETTINGS);
    public static final Item CLAY_HOE_CAST = register("clay_hoe_cast", CAST_SETTINGS);
    public static final Item CLAY_SHIELD_CAST = register("clay_shield_cast", CAST_SETTINGS);
    public static final Item CLAY_ANVIL_CAST = register("clay_anvil_cast", CAST_SETTINGS);
    public static final Item CLAY_TOTEM_CAST = register("clay_totem_cast", CAST_SETTINGS);

    public static final Item SWORD_CAST = registerCast(
            "sword",
            CraftingPattern.SWORD,
            GenesisTags.Items.FROM_SWORD_CAST
    );
    public static final Item SHOVEL_CAST = registerCast(
            "shovel",
            CraftingPattern.SHOVEL,
            GenesisTags.Items.FROM_SHOVEL_CAST
    );
    public static final Item PICKAXE_CAST = registerCast(
            "pickaxe",
            CraftingPattern.PICKAXE,
            GenesisTags.Items.FROM_PICKAXE_CAST
    );
    public static final Item AXE_CAST = registerCast(
            "axe",
            CraftingPattern.AXE,
            GenesisTags.Items.FROM_AXE_CAST
    );
    public static final Item HOE_CAST = registerCast(
            "hoe",
            CraftingPattern.HOE,
            GenesisTags.Items.FROM_HOE_CAST
    );
    public static final Item SHIELD_CAST = registerCast(
            "shield",
            CraftingPattern.SHIELD,
            GenesisTags.Items.FROM_SHIELD_CAST
    );
    public static final Item ANVIL_CAST = registerCast(
            "anvil",
            CraftingPattern.ANVIL,
            GenesisTags.Items.FROM_ANVIL_CAST
    );
    public static final Item TOTEM_CAST = registerCast(
            "totem",
            CraftingPattern.ALL,
            GenesisTags.Items.FROM_TOTEM_CAST
    );

    public static final Item HEALTHY_STEW = register("healthy_stew", (
                    new Item.Settings()
                            .maxCount(GenesisConstants.STEW_STACK_SIZE)
                            .food(GenesisFoodComponents.HEALTHY_STEW, GenesisConsumableComponents.HEALTHY_STEW)
                            .useRemainder(Items.BOWL)
            )
    );

    public static final Item ENCHANTED_HONEY_BOTTLE = register("enchanted_honey_bottle", (new Item.Settings()
                    .recipeRemainder(Items.GLASS_BOTTLE)
                    .food(FoodComponents.HONEY_BOTTLE, GenesisConsumableComponents.ENCHANTED_HONEY_BOTTLE)
                    .useRemainder(Items.GLASS_BOTTLE)
                    .component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .rarity(Rarity.UNCOMMON)
                    .maxCount(16)
            )
    );

    public static final Item BOAR_SPAWN_EGG = register(
            "boar_spawn_egg",
            settings -> new SpawnEggItem(GenesisEntities.BOAR, settings)
    );

    private static RegistryKey<Item> keyOf(String id) {
        return RegistryKey.of(RegistryKeys.ITEM, Genesis.id(id));
    }

    private static Item registerCast(String type, CraftingPattern pattern, TagKey<Item> crafts) {
        return register(
                type + "_cast",
                (settings -> new AssemblyPatternItem(pattern, crafts, settings)),
                CAST_SETTINGS
        );
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
        Genesis.LOGGER.info("Registering Items for " + Genesis.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.addAfter(Items.IRON_NUGGET, COPPER_NUGGET);

            entries.addAfter(Items.RAW_GOLD, RAW_NETHERITE);
            entries.addAfter(RAW_NETHERITE, RAW_COAL);
            entries.addAfter(RAW_COAL, RAW_EMERALD);
            entries.addAfter(RAW_EMERALD, RAW_LAPIS_LAZULI);
            entries.addAfter(RAW_LAPIS_LAZULI, RAW_DIAMOND);
            entries.addAfter(RAW_DIAMOND, RAW_REDSTONE);

            entries.addBefore(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, BLANK_CLAY_CAST);
            entries.addAfter(BLANK_CLAY_CAST, CLAY_SWORD_CAST);
            entries.addAfter(CLAY_SWORD_CAST, CLAY_SHOVEL_CAST);
            entries.addAfter(CLAY_SHOVEL_CAST, CLAY_PICKAXE_CAST);
            entries.addAfter(CLAY_PICKAXE_CAST, CLAY_AXE_CAST);
            entries.addAfter(CLAY_AXE_CAST, CLAY_HOE_CAST);
            entries.addAfter(CLAY_HOE_CAST, CLAY_SHIELD_CAST);
            entries.addAfter(CLAY_SHIELD_CAST, CLAY_ANVIL_CAST);
            entries.addAfter(CLAY_ANVIL_CAST, CLAY_TOTEM_CAST);

            entries.addAfter(CLAY_TOTEM_CAST, SWORD_CAST);
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

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            entries.addAfter(Items.RABBIT_STEW, HEALTHY_STEW);
            entries.addAfter(Items.HONEY_BOTTLE, ENCHANTED_HONEY_BOTTLE);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries ->
                entries.addAfter(Items.TURTLE_SPAWN_EGG, BOAR_SPAWN_EGG)
        );
    }
}

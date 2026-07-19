package dev.mariany.genesis.item;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.component.type.GenesisConsumableComponents;
import dev.mariany.genesis.entity.GenesisEntityTypes;
import dev.mariany.genesis.item.custom.AssemblyPatternItem;
import dev.mariany.genesis.item.custom.FlintsItem;
import dev.mariany.genesis.recipe.CraftingPattern;
import dev.mariany.genesis.tag.GenesisTags;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.*;

import java.util.function.Function;

public class GenesisItems {
    private static final Item.Properties CAST_SETTINGS = new Item.Properties().stacksTo(1);

    public static final Item FLINTS = register("flints", FlintsItem::new, (new Item.Properties()).durability(4));

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
    public static final Item CLAY_SPEAR_CAST = register("clay_spear_cast", CAST_SETTINGS);
    public static final Item CLAY_SHIELD_CAST = register("clay_shield_cast", CAST_SETTINGS);
    public static final Item CLAY_ANVIL_CAST = register("clay_anvil_cast", CAST_SETTINGS);
    public static final Item CLAY_TOTEM_CAST = register("clay_totem_cast", CAST_SETTINGS.rarity(Rarity.UNCOMMON));

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

    public static final Item SPEAR_CAST = registerCast(
            "spear",
            CraftingPattern.SPEAR,
            GenesisTags.Items.FROM_SPEAR_CAST
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
            GenesisTags.Items.FROM_TOTEM_CAST,
            Rarity.UNCOMMON
    );

    public static final Item ENCHANTED_HONEY_BOTTLE = register(
            "enchanted_honey_bottle", (new Item.Properties()
                    .craftRemainder(Items.GLASS_BOTTLE)
                    .food(Foods.HONEY_BOTTLE, GenesisConsumableComponents.ENCHANTED_HONEY_BOTTLE)
                    .usingConvertsTo(Items.GLASS_BOTTLE)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .rarity(Rarity.UNCOMMON)
                    .stacksTo(16)
            )
    );

    public static final Item BOAR_SPAWN_EGG = register(
            "boar_spawn_egg",
            SpawnEggItem::new,
            new Item.Properties().spawnEgg(GenesisEntityTypes.BOAR)
    );

    private static ResourceKey<Item> keyOf(String id) {
        return ResourceKey.create(Registries.ITEM, Genesis.id(id));
    }

    private static Item registerCast(String type, CraftingPattern pattern, TagKey<Item> crafts) {
        return registerCast(type, pattern, crafts, Rarity.COMMON);
    }

    private static Item registerCast(String type, CraftingPattern pattern, TagKey<Item> crafts, Rarity rarity) {
        return register(
                type + "_cast",
                (settings -> new AssemblyPatternItem(pattern, crafts, settings)),
                CAST_SETTINGS.rarity(rarity)
        );
    }

    private static Item register(String name) {
        return register(name, Item::new, new Item.Properties());
    }

    private static Item register(String name, Item.Properties settings) {
        return register(name, Item::new, settings);
    }

    private static Item register(String name, Function<Item.Properties, Item> factory, Item.Properties settings) {
        ResourceKey<Item> itemKey = keyOf(name);
        Item item = factory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
    }

    public static void bootstrap() {
        Genesis.bootstrapLog("Items");

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(entries -> {
            entries.insertAfter(Items.RAW_GOLD, RAW_NETHERITE);
            entries.insertAfter(RAW_NETHERITE, RAW_COAL);
            entries.insertAfter(RAW_COAL, RAW_EMERALD);
            entries.insertAfter(RAW_EMERALD, RAW_LAPIS_LAZULI);
            entries.insertAfter(RAW_LAPIS_LAZULI, RAW_DIAMOND);
            entries.insertAfter(RAW_DIAMOND, RAW_REDSTONE);

            entries.insertBefore(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, BLANK_CLAY_CAST);
            entries.insertAfter(BLANK_CLAY_CAST, CLAY_SWORD_CAST);
            entries.insertAfter(CLAY_SWORD_CAST, CLAY_SHOVEL_CAST);
            entries.insertAfter(CLAY_SHOVEL_CAST, CLAY_PICKAXE_CAST);
            entries.insertAfter(CLAY_PICKAXE_CAST, CLAY_AXE_CAST);
            entries.insertAfter(CLAY_AXE_CAST, CLAY_HOE_CAST);
            entries.insertAfter(CLAY_HOE_CAST, CLAY_SPEAR_CAST);
            entries.insertAfter(CLAY_SPEAR_CAST, CLAY_SHIELD_CAST);
            entries.insertAfter(CLAY_SHIELD_CAST, CLAY_ANVIL_CAST);
            entries.insertAfter(CLAY_ANVIL_CAST, CLAY_TOTEM_CAST);

            entries.insertAfter(CLAY_TOTEM_CAST, SWORD_CAST);
            entries.insertAfter(SWORD_CAST, SHOVEL_CAST);
            entries.insertAfter(SHOVEL_CAST, PICKAXE_CAST);
            entries.insertAfter(PICKAXE_CAST, AXE_CAST);
            entries.insertAfter(AXE_CAST, HOE_CAST);
            entries.insertAfter(HOE_CAST, SPEAR_CAST);
            entries.insertAfter(SPEAR_CAST, SHIELD_CAST);
            entries.insertAfter(SHIELD_CAST, ANVIL_CAST);
            entries.insertAfter(ANVIL_CAST, TOTEM_CAST);
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries -> {
            entries.insertAfter(Items.FLINT_AND_STEEL, FLINTS);
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(entries -> {
            entries.insertAfter(Items.HONEY_BOTTLE, ENCHANTED_HONEY_BOTTLE);
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SPAWN_EGGS).register(entries -> {
            entries.insertAfter(
                    Items.TURTLE_SPAWN_EGG,
                    BOAR_SPAWN_EGG
            );
        });
    }
}

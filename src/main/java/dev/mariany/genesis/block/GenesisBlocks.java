package dev.mariany.genesis.block;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.block.custom.AssemblyTableBlock;
import dev.mariany.genesis.block.custom.KilnBlock;
import dev.mariany.genesis.block.custom.cauldron.FilledPrimitiveCauldronBlock;
import dev.mariany.genesis.block.custom.cauldron.PrimitiveCauldronBehavior;
import dev.mariany.genesis.block.custom.cauldron.PrimitiveCauldronBlock;
import dev.mariany.genesis.loot.GenesisLootTables;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Function;

public class GenesisBlocks {
    public static final Block CLAY_KILN = register(
            "clay_kiln",
            BlockBehaviour.Properties.of()
                                     .mapColor(MapColor.CLAY)
                                     .sound(SoundType.GRAVEL)
                                     .strength(0.6F)
    );

    public static final Block KILN = register(
            "kiln",
            KilnBlock::new,
            BlockBehaviour.Properties.of()
                                     .mapColor(MapColor.COLOR_RED)
                                     .requiresCorrectToolForDrops()
                                     .strength(2.0F, 6.0F)
    );

    public static final Block CLAY_CAULDRON = register(
            "clay_cauldron",
            PrimitiveCauldronBlock::new,
            BlockBehaviour.Properties.of()
                                     .mapColor(MapColor.CLAY)
                                     .sound(SoundType.GRAVEL)
                                     .strength(0.6F)
    );

    public static final Block TERRACOTTA_CAULDRON = register(
            "terracotta_cauldron",
            settings -> new PrimitiveCauldronBlock(PrimitiveCauldronBehavior.EMPTY_CAULDRON_BEHAVIOR, settings),
            BlockBehaviour.Properties.of()
                                     .mapColor(MapColor.COLOR_ORANGE)
                                     .sound(SoundType.PACKED_MUD)
                                     .strength(0.6F)
    );

    public static final Block DIRT_TERRACOTTA_CAULDRON = register(
            "dirt_terracotta_cauldron",
            settings -> new FilledPrimitiveCauldronBlock(
                    TERRACOTTA_CAULDRON,
                    Blocks.DIRT,
                    SoundEvents.BRUSH_GRAVEL,
                    SoundEvents.BRUSH_GRAVEL_COMPLETED,
                    GenesisLootTables.DIRT_DUSTING,
                    settings
            ),
            BlockBehaviour.Properties.ofFullCopy(TERRACOTTA_CAULDRON)
    );

    public static final Block GRAVEL_TERRACOTTA_CAULDRON = register(
            "gravel_terracotta_cauldron",
            settings -> new FilledPrimitiveCauldronBlock(
                    TERRACOTTA_CAULDRON,
                    Blocks.GRAVEL,
                    SoundEvents.BRUSH_GRAVEL,
                    SoundEvents.BRUSH_GRAVEL_COMPLETED,
                    GenesisLootTables.GRAVEL_DUSTING,
                    settings
            ),
            BlockBehaviour.Properties.ofFullCopy(TERRACOTTA_CAULDRON)
    );

    public static final Block SOUL_SAND_TERRACOTTA_CAULDRON = register(
            "soul_sand_terracotta_cauldron",
            settings -> new FilledPrimitiveCauldronBlock(
                    TERRACOTTA_CAULDRON,
                    Blocks.SOUL_SAND,
                    SoundEvents.BRUSH_SAND,
                    SoundEvents.BRUSH_SAND_COMPLETED,
                    GenesisLootTables.SOUL_SEDIMENT_DUSTING,
                    settings
            ),
            BlockBehaviour.Properties.ofFullCopy(TERRACOTTA_CAULDRON)
    );

    public static final Block SOUL_SOIL_TERRACOTTA_CAULDRON = register(
            "soul_soil_terracotta_cauldron",
            settings -> new FilledPrimitiveCauldronBlock(
                    TERRACOTTA_CAULDRON,
                    Blocks.SOUL_SAND,
                    SoundEvents.BRUSH_GRAVEL,
                    SoundEvents.BRUSH_GRAVEL_COMPLETED,
                    GenesisLootTables.SOUL_SEDIMENT_DUSTING,
                    settings
            ),
            BlockBehaviour.Properties.ofFullCopy(TERRACOTTA_CAULDRON)
    );

    public static final Block RAW_COAL_BLOCK = register(
            "raw_coal_block",
            BlockBehaviour.Properties.of()
                                     .mapColor(MapColor.COLOR_BLACK)
                                     .instrument(NoteBlockInstrument.BASEDRUM)
                                     .requiresCorrectToolForDrops()
                                     .strength(5F, 6F)
    );

    public static final Block RAW_DIAMOND_BLOCK = register(
            "raw_diamond_block",
            BlockBehaviour.Properties.ofFullCopy(RAW_COAL_BLOCK)
                                     .mapColor(MapColor.DIAMOND)
                                     .sound(SoundType.NETHER_ORE)
    );

    public static final Block RAW_EMERALD_BLOCK = register(
            "raw_emerald_block",
            BlockBehaviour.Properties.ofFullCopy(RAW_COAL_BLOCK)
                                     .sound(SoundType.NETHER_ORE)
                                     .mapColor(MapColor.EMERALD)
    );

    public static final Block RAW_LAPIS_LAZULI_BLOCK = register(
            "raw_lapis_lazuli_block",
            BlockBehaviour.Properties.ofFullCopy(RAW_COAL_BLOCK).mapColor(MapColor.LAPIS)
    );

    public static final Block RAW_NETHERITE_BLOCK = register(
            "raw_netherite_block",
            BlockBehaviour.Properties.ofFullCopy(RAW_COAL_BLOCK)
                                     .strength(30, 1200)
                                     .sound(SoundType.ANCIENT_DEBRIS)
    );

    public static final Block RAW_REDSTONE_BLOCK = register(
            "raw_redstone_block",
            BlockBehaviour.Properties.ofFullCopy(RAW_COAL_BLOCK).mapColor(MapColor.FIRE)
    );

    public static final Block ASSEMBLY_TABLE = register(
            "assembly_table",
            AssemblyTableBlock::new,
            BlockBehaviour.Properties.of()
                                     .mapColor(MapColor.WOOD)
                                     .instrument(NoteBlockInstrument.BASS)
                                     .strength(2.5F)
                                     .sound(SoundType.WOOD)
                                     .ignitedByLava()
    );

    private static Block register(
            String name,
            BlockBehaviour.Properties settings
    ) {
        return register(name, Block::new, settings);
    }

    private static Block register(
            String name, Function<BlockBehaviour.Properties, Block> factory,
            BlockBehaviour.Properties settings
    ) {
        final Identifier identifier = Genesis.id(name);

        final ResourceKey<Block> registryKey = ResourceKey.create(Registries.BLOCK, identifier);
        final ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, identifier);

        final Block block = Blocks.register(registryKey, factory, settings);

        Registry.register(
                BuiltInRegistries.ITEM,
                itemKey,
                new BlockItem(
                        block,
                        new Item.Properties()
                                .setId(itemKey)
                                .useBlockDescriptionPrefix()
                                .requiredFeatures(block.requiredFeatures())
                )
        );

        return block;
    }

    public static void bootstrap() {
        Genesis.bootstrapLog("Blocks");

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
            entries.insertAfter(Items.CRAFTING_TABLE, ASSEMBLY_TABLE);
            entries.insertBefore(Items.CAMPFIRE, CLAY_CAULDRON);
            entries.insertAfter(CLAY_CAULDRON, TERRACOTTA_CAULDRON);
            entries.insertAfter(TERRACOTTA_CAULDRON, DIRT_TERRACOTTA_CAULDRON);
            entries.insertAfter(DIRT_TERRACOTTA_CAULDRON, GRAVEL_TERRACOTTA_CAULDRON);
            entries.insertAfter(GRAVEL_TERRACOTTA_CAULDRON, SOUL_SAND_TERRACOTTA_CAULDRON);
            entries.insertAfter(SOUL_SAND_TERRACOTTA_CAULDRON, SOUL_SOIL_TERRACOTTA_CAULDRON);
            entries.insertAfter(SOUL_SOIL_TERRACOTTA_CAULDRON, CLAY_KILN);
            entries.insertAfter(CLAY_KILN, KILN);
        });

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register(entries -> {
            entries.insertBefore(Items.RAW_IRON_BLOCK, RAW_NETHERITE_BLOCK);
            entries.insertAfter(RAW_NETHERITE_BLOCK, RAW_COAL_BLOCK);
            entries.insertAfter(RAW_COAL_BLOCK, RAW_DIAMOND_BLOCK);
            entries.insertAfter(RAW_DIAMOND_BLOCK, RAW_EMERALD_BLOCK);
            entries.insertAfter(RAW_EMERALD_BLOCK, RAW_LAPIS_LAZULI_BLOCK);
            entries.insertAfter(RAW_LAPIS_LAZULI_BLOCK, RAW_REDSTONE_BLOCK);
        });
    }
}

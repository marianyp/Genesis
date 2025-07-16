package dev.mariany.genesis.block;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.block.custom.KilnBlock;
import dev.mariany.genesis.block.custom.cauldron.FilledPrimitiveCauldronBlock;
import dev.mariany.genesis.block.custom.cauldron.PrimitiveCauldronBehavior;
import dev.mariany.genesis.block.custom.cauldron.PrimitiveCauldronBlock;
import dev.mariany.genesis.loot.GenesisLootTables;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class GenesisBlocks {
    public static final Block CLAY_KILN = register("clay_kiln",
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.LIGHT_BLUE_GRAY)
                    .sounds(BlockSoundGroup.GRAVEL)
                    .strength(0.6F)
    );

    public static final Block KILN = register("kiln", KilnBlock::new,
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.RED)
                    .requiresTool()
                    .strength(2.0F, 6.0F)
    );

    public static final Block CLAY_CAULDRON = register("clay_cauldron", PrimitiveCauldronBlock::new,
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.LIGHT_BLUE_GRAY)
                    .sounds(BlockSoundGroup.GRAVEL)
                    .strength(0.6F)
    );

    public static final Block TERRACOTTA_CAULDRON = register("terracotta_cauldron",
            settings -> new PrimitiveCauldronBlock(PrimitiveCauldronBehavior.EMPTY_CAULDRON_BEHAVIOR, settings),
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.ORANGE)
                    .sounds(BlockSoundGroup.PACKED_MUD)
                    .strength(0.6F)
    );

    public static final Block DIRT_TERRACOTTA_CAULDRON = register("dirt_terracotta_cauldron",
            settings -> new FilledPrimitiveCauldronBlock(
                    TERRACOTTA_CAULDRON,
                    Blocks.DIRT,
                    SoundEvents.ITEM_BRUSH_BRUSHING_GRAVEL,
                    SoundEvents.ITEM_BRUSH_BRUSHING_GRAVEL_COMPLETE,
                    GenesisLootTables.DIRT_DUSTING,
                    settings
            ),
            AbstractBlock.Settings.copy(TERRACOTTA_CAULDRON)
    );

    public static final Block GRAVEL_TERRACOTTA_CAULDRON = register("gravel_terracotta_cauldron",
            settings -> new FilledPrimitiveCauldronBlock(
                    TERRACOTTA_CAULDRON,
                    Blocks.GRAVEL,
                    SoundEvents.ITEM_BRUSH_BRUSHING_GRAVEL,
                    SoundEvents.ITEM_BRUSH_BRUSHING_GRAVEL_COMPLETE,
                    GenesisLootTables.GRAVEL_DUSTING,
                    settings
            ),
            AbstractBlock.Settings.copy(TERRACOTTA_CAULDRON)
    );

    public static final Block SOUL_SAND_TERRACOTTA_CAULDRON = register("soul_sand_terracotta_cauldron",
            settings -> new FilledPrimitiveCauldronBlock(
                    TERRACOTTA_CAULDRON,
                    Blocks.SOUL_SAND,
                    SoundEvents.ITEM_BRUSH_BRUSHING_SAND,
                    SoundEvents.ITEM_BRUSH_BRUSHING_SAND_COMPLETE,
                    GenesisLootTables.SOUL_SEDIMENT_DUSTING,
                    settings
            ),
            AbstractBlock.Settings.copy(TERRACOTTA_CAULDRON)
    );

    public static final Block SOUL_SOIL_TERRACOTTA_CAULDRON = register("soul_soil_terracotta_cauldron",
            settings -> new FilledPrimitiveCauldronBlock(
                    TERRACOTTA_CAULDRON,
                    Blocks.SOUL_SAND,
                    SoundEvents.ITEM_BRUSH_BRUSHING_GRAVEL,
                    SoundEvents.ITEM_BRUSH_BRUSHING_GRAVEL_COMPLETE,
                    GenesisLootTables.SOUL_SEDIMENT_DUSTING,
                    settings
            ),
            AbstractBlock.Settings.copy(TERRACOTTA_CAULDRON)
    );

    public static final Block RAW_COAL_BLOCK = register(
            "raw_coal_block",
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.BLACK)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresTool()
                    .strength(5F, 6F)
    );

    public static final Block RAW_DIAMOND_BLOCK = register(
            "raw_diamond_block", AbstractBlock.Settings.copy(RAW_COAL_BLOCK).mapColor(MapColor.DIAMOND_BLUE)
    );

    public static final Block RAW_EMERALD_BLOCK = register(
            "raw_emerald_block", AbstractBlock.Settings.copy(RAW_COAL_BLOCK).mapColor(MapColor.EMERALD_GREEN)
    );

    public static final Block RAW_LAPIS_LAZULI_BLOCK = register(
            "raw_lapis_lazuli_block", AbstractBlock.Settings.copy(RAW_COAL_BLOCK).mapColor(MapColor.LAPIS_BLUE)
    );

    public static final Block RAW_NETHERITE_BLOCK = register(
            "raw_netherite_block", AbstractBlock.Settings.copy(RAW_COAL_BLOCK)
                    .strength(30.0F, 1200.0F)
                    .sounds(BlockSoundGroup.ANCIENT_DEBRIS)
    );

    public static final Block RAW_REDSTONE_BLOCK = register(
            "raw_redstone_block", AbstractBlock.Settings.copy(RAW_COAL_BLOCK).mapColor(MapColor.BRIGHT_RED)
    );

    private static Block register(String name,
                                  AbstractBlock.Settings settings) {
        return register(name, Block::new, settings);
    }

    private static Block register(String name, Function<AbstractBlock.Settings, Block> factory,
                                  AbstractBlock.Settings settings) {
        final Identifier identifier = Genesis.id(name);
        final RegistryKey<Block> registryKey = RegistryKey.of(RegistryKeys.BLOCK, identifier);

        final Block block = Blocks.register(registryKey, factory, settings);
        Items.register(block);

        return block;
    }

    public static void bootstrap() {
        Genesis.LOGGER.info("Registering Blocks for " + Genesis.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.addBefore(Items.CAMPFIRE, CLAY_CAULDRON);
            entries.addAfter(CLAY_CAULDRON, TERRACOTTA_CAULDRON);
            entries.addAfter(TERRACOTTA_CAULDRON, DIRT_TERRACOTTA_CAULDRON);
            entries.addAfter(DIRT_TERRACOTTA_CAULDRON, GRAVEL_TERRACOTTA_CAULDRON);
            entries.addAfter(GRAVEL_TERRACOTTA_CAULDRON, SOUL_SAND_TERRACOTTA_CAULDRON);
            entries.addAfter(SOUL_SAND_TERRACOTTA_CAULDRON, SOUL_SOIL_TERRACOTTA_CAULDRON);
            entries.addAfter(SOUL_SOIL_TERRACOTTA_CAULDRON, CLAY_KILN);
            entries.addAfter(CLAY_KILN, KILN);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(entries -> {
            entries.addBefore(Items.RAW_IRON_BLOCK, RAW_NETHERITE_BLOCK);
            entries.addAfter(RAW_NETHERITE_BLOCK, RAW_COAL_BLOCK);
            entries.addAfter(RAW_COAL_BLOCK, RAW_DIAMOND_BLOCK);
            entries.addAfter(RAW_DIAMOND_BLOCK, RAW_EMERALD_BLOCK);
            entries.addAfter(RAW_EMERALD_BLOCK, RAW_LAPIS_LAZULI_BLOCK);
            entries.addAfter(RAW_LAPIS_LAZULI_BLOCK, RAW_REDSTONE_BLOCK);
        });
    }
}

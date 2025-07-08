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
                    SoundEvents.ITEM_BRUSH_BRUSHING_GRAVEL,
                    GenesisLootTables.CLAY_SIFTING,
                    settings
            ),
            AbstractBlock.Settings.copy(TERRACOTTA_CAULDRON)
    );

    public static final Block GRAVEL_TERRACOTTA_CAULDRON = register("gravel_terracotta_cauldron",
            settings -> new FilledPrimitiveCauldronBlock(
                    TERRACOTTA_CAULDRON,
                    Blocks.GRAVEL,
                    SoundEvents.ITEM_BRUSH_BRUSHING_GRAVEL,
                    SoundEvents.ITEM_BRUSH_BRUSHING_GRAVEL,
                    GenesisLootTables.FLINT_SIFTING,
                    settings
            ),
            AbstractBlock.Settings.copy(TERRACOTTA_CAULDRON)
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
            entries.addAfter(GRAVEL_TERRACOTTA_CAULDRON, CLAY_KILN);
            entries.addAfter(CLAY_KILN, KILN);
        });
    }
}

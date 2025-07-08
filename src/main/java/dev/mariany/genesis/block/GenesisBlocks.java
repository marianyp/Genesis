package dev.mariany.genesis.block;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.block.custom.KilnBlock;
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
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class GenesisBlocks {
    public static final Block CLAY_KILN = register("clay_kiln",
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.LIGHT_BLUE_GRAY)
                    .sounds(BlockSoundGroup.PACKED_MUD)
                    .strength(0.6F)
    );

    public static final Block KILN = register("kiln", KilnBlock::new,
            AbstractBlock.Settings.create()
                    .mapColor(MapColor.RED)
                    .requiresTool()
                    .strength(2.0F, 6.0F)
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
        Genesis.LOGGER.info("Registering blocks for " + Genesis.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.addBefore(Items.CAMPFIRE, CLAY_KILN);
            entries.addAfter(CLAY_KILN, KILN);
        });
    }
}

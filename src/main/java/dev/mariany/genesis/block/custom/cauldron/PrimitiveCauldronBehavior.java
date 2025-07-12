package dev.mariany.genesis.block.custom.cauldron;

import dev.mariany.genesis.block.GenesisBlocks;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface PrimitiveCauldronBehavior {
    Map<String, PrimitiveCauldronBehaviorMap> BEHAVIOR_MAPS = new Object2ObjectArrayMap<>();

    PrimitiveCauldronBehaviorMap EMPTY_CAULDRON_BEHAVIOR = createMap("empty");

    static PrimitiveCauldronBehaviorMap createMap(String name) {
        List<PrimitiveCauldronBehaviorEntry> entries = new ArrayList<>();
        return new PrimitiveCauldronBehaviorMap(name, entries);
    }

    ActionResult interact(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack);

    static void registerBehavior() {
        RegistryEntryLookup<Item> registryEntryLookup = Registries.createEntryLookup(Registries.ITEM);

        RegistryEntryList.Named<Item> dirtItems = registryEntryLookup.getOrThrow(ItemTags.DIRT);
        RegistryEntryList.Named<Item> gravelItems = registryEntryLookup.getOrThrow(ConventionalItemTags.GRAVELS);

        EMPTY_CAULDRON_BEHAVIOR.entries().add(
                new PrimitiveCauldronBehaviorEntry(
                        Ingredient.ofTag(dirtItems),
                        PrimitiveCauldronBehavior::tryFillWithDirt
                )
        );

        EMPTY_CAULDRON_BEHAVIOR.entries().add(
                new PrimitiveCauldronBehaviorEntry(
                        Ingredient.ofTag(gravelItems),
                        PrimitiveCauldronBehavior::tryFillWithGravel
                )
        );
    }

    static ActionResult fillCauldron(World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, BlockState state, SoundEvent soundEvent) {
        if (!world.isClient) {
            stack.decrementUnlessCreative(1, player);
//            player.incrementStat(Stats.FILL_CAULDRON);
            player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
            world.setBlockState(pos, state);
            world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1F, 1F);
            world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, pos);
        }

        return ActionResult.SUCCESS;
    }

    private static ActionResult tryFillWithDirt(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
        return fillCauldron(
                world,
                pos,
                player,
                hand,
                stack,
                GenesisBlocks.DIRT_TERRACOTTA_CAULDRON.getDefaultState(),
                SoundEvents.BLOCK_GRAVEL_PLACE
        );
    }

    private static ActionResult tryFillWithGravel(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
        return fillCauldron(
                world,
                pos,
                player,
                hand,
                stack,
                GenesisBlocks.GRAVEL_TERRACOTTA_CAULDRON.getDefaultState(),
                SoundEvents.BLOCK_GRAVEL_PLACE
        );
    }

    record PrimitiveCauldronBehaviorEntry(Ingredient ingredient, PrimitiveCauldronBehavior behavior) {
    }

    record PrimitiveCauldronBehaviorMap(String name, List<PrimitiveCauldronBehaviorEntry> entries) {
    }
}

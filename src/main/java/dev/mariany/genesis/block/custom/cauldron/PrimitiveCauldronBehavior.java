package dev.mariany.genesis.block.custom.cauldron;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.block.GenesisBlocks;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface PrimitiveCauldronBehavior {
    Map<String, PrimitiveCauldronBehaviorMap> BEHAVIOR_MAPS = new Object2ObjectArrayMap<>();

    PrimitiveCauldronBehaviorMap EMPTY_CAULDRON_BEHAVIOR = createMap("empty");

    static PrimitiveCauldronBehaviorMap createMap(String name) {
        List<PrimitiveCauldronBehaviorEntry> entries = new ArrayList<>();
        PrimitiveCauldronBehaviorMap primitiveCauldronBehaviorMap = new PrimitiveCauldronBehaviorMap(name, entries);
        BEHAVIOR_MAPS.put(name, primitiveCauldronBehaviorMap);
        return primitiveCauldronBehaviorMap;
    }

    InteractionResult interact(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            ItemStack stack
    );

    static void bootstrap() {
        Genesis.bootstrapLog("Primitive Cauldron Behaviors");

        HolderGetter<Item> registryEntryLookup =
                BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.ITEM);

        HolderSet.Named<Item> dirtItems = registryEntryLookup.getOrThrow(ItemTags.DIRT);
        HolderSet.Named<Item> gravelItems = registryEntryLookup.getOrThrow(ConventionalItemTags.GRAVELS);

        EMPTY_CAULDRON_BEHAVIOR.entries().add(
                new PrimitiveCauldronBehaviorEntry(
                        Ingredient.of(dirtItems),
                        PrimitiveCauldronBehavior::tryFillWithDirt
                )
        );

        EMPTY_CAULDRON_BEHAVIOR.entries().add(
                new PrimitiveCauldronBehaviorEntry(
                        Ingredient.of(gravelItems),
                        PrimitiveCauldronBehavior::tryFillWithGravel
                )
        );

        EMPTY_CAULDRON_BEHAVIOR.entries().add(
                new PrimitiveCauldronBehaviorEntry(
                        Ingredient.of(Blocks.SOUL_SAND),
                        PrimitiveCauldronBehavior::tryFillWithSoulSand
                )
        );

        EMPTY_CAULDRON_BEHAVIOR.entries().add(
                new PrimitiveCauldronBehaviorEntry(
                        Ingredient.of(Blocks.SOUL_SOIL),
                        PrimitiveCauldronBehavior::tryFillWithSoulSoil
                )
        );
    }

    static InteractionResult fillCauldron(
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            ItemStack stack,
            BlockState state,
            SoundEvent soundEvent
    ) {
        if (!level.isClientSide()) {
            stack.consume(1, player);
            player.awardStat(Stats.FILL_CAULDRON);
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            level.setBlockAndUpdate(pos, state);
            level.playSound(null, pos, soundEvent, SoundSource.BLOCKS, 1F, 1F);
            level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
        }

        return InteractionResult.SUCCESS;
    }

    private static InteractionResult tryFillWithDirt(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            ItemStack stack
    ) {
        return fillCauldron(
                level,
                pos,
                player,
                hand,
                stack,
                GenesisBlocks.DIRT_TERRACOTTA_CAULDRON.defaultBlockState(),
                SoundEvents.GRAVEL_PLACE
        );
    }

    private static InteractionResult tryFillWithGravel(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            ItemStack stack
    ) {
        return fillCauldron(
                level,
                pos,
                player,
                hand,
                stack,
                GenesisBlocks.GRAVEL_TERRACOTTA_CAULDRON.defaultBlockState(),
                SoundEvents.GRAVEL_PLACE
        );
    }

    private static InteractionResult tryFillWithSoulSand(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            ItemStack stack
    ) {
        return fillCauldron(
                level,
                pos,
                player,
                hand,
                stack,
                GenesisBlocks.SOUL_SAND_TERRACOTTA_CAULDRON.defaultBlockState(),
                SoundEvents.SOUL_SAND_PLACE
        );
    }

    private static InteractionResult tryFillWithSoulSoil(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            ItemStack stack
    ) {
        return fillCauldron(
                level,
                pos,
                player,
                hand,
                stack,
                GenesisBlocks.SOUL_SOIL_TERRACOTTA_CAULDRON.defaultBlockState(),
                SoundEvents.SOUL_SOIL_PLACE
        );
    }

    record PrimitiveCauldronBehaviorEntry(Ingredient ingredient, PrimitiveCauldronBehavior behavior) {
    }

    record PrimitiveCauldronBehaviorMap(String name, List<PrimitiveCauldronBehaviorEntry> entries) {
    }
}

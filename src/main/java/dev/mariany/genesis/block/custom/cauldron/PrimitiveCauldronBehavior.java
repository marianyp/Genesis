package dev.mariany.genesis.block.custom.cauldron;

import dev.mariany.genesis.block.GenesisBlocks;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Map;

public interface PrimitiveCauldronBehavior {
    Map<String, PrimitiveCauldronBehaviorMap> BEHAVIOR_MAPS = new Object2ObjectArrayMap<>();

    PrimitiveCauldronBehaviorMap EMPTY_CAULDRON_BEHAVIOR = createMap("empty");

    static PrimitiveCauldronBehaviorMap createMap(String name) {
        Object2ObjectOpenHashMap<Item, PrimitiveCauldronBehavior> object2ObjectOpenHashMap = new Object2ObjectOpenHashMap<>();
        object2ObjectOpenHashMap.defaultReturnValue((state, world, pos, player, hand, stack) -> ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION);
        PrimitiveCauldronBehaviorMap cauldronBehaviorMap = new PrimitiveCauldronBehaviorMap(name, object2ObjectOpenHashMap);
        BEHAVIOR_MAPS.put(name, cauldronBehaviorMap);
        return cauldronBehaviorMap;
    }

    ActionResult interact(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack);

    static void registerBehavior() {
        Map<Item, PrimitiveCauldronBehavior> behavior = EMPTY_CAULDRON_BEHAVIOR.map();
        behavior.put(Items.DIRT, PrimitiveCauldronBehavior::tryFillWithDirt);
        behavior.put(Items.GRAVEL, PrimitiveCauldronBehavior::tryFillWithGravel);
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

    record PrimitiveCauldronBehaviorMap(String name, Map<Item, PrimitiveCauldronBehavior> map) {
    }
}

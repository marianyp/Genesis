package dev.mariany.genesis.event.block;

import dev.mariany.genesis.age.AgeEntry;
import dev.mariany.genesis.age.AgeHelpers;
import dev.mariany.genesis.age.AgeManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

import java.util.Optional;

public class UseBlockHandler {
    public static ActionResult onUseBlock(PlayerEntity player, World world, Hand hand, BlockHitResult result) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            AgeManager ageManager = AgeManager.getInstance();
            BlockState state = world.getBlockState(result.getBlockPos());
            Block block = state.getBlock();

            if (!ageManager.isUnlocked(serverPlayer, block)) {
                String itemTranslation = block.getName().getString();

                Optional<AgeEntry> optionalAgeEntry = ageManager.getRequiredAges(block.asItem().getDefaultStack())
                        .stream()
                        .findAny();

                optionalAgeEntry.ifPresent(ageEntry ->
                        AgeHelpers.notifyAgeLocked(itemTranslation, ageEntry.getAge(), serverPlayer)
                );

                return ActionResult.FAIL;
            }
        }

        return ActionResult.PASS;
    }
}

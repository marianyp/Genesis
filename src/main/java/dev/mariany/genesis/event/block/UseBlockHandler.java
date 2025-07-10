package dev.mariany.genesis.event.block;

import dev.mariany.genesis.age.AgeManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class UseBlockHandler {
    public static ActionResult onUseBlock(PlayerEntity player, World world, Hand hand, BlockHitResult result) {
        if (player instanceof ServerPlayerEntity serverPlayerEntity) {
            AgeManager ageManager = AgeManager.getInstance();
            BlockState state = world.getBlockState(result.getBlockPos());

            if (!ageManager.isUnlocked(serverPlayerEntity, state)) {
                return ActionResult.FAIL;
            }
        }

        return ActionResult.PASS;
    }
}

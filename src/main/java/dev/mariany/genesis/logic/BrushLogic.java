package dev.mariany.genesis.logic;

import dev.mariany.genesis.block.custom.cauldron.FilledPrimitiveCauldronBlock;
import dev.mariany.genesis.block.custom.cauldron.PrimitiveCauldronBlock;
import dev.mariany.genesis.block.entity.custom.FilledPrimitiveCauldronBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Optional;

public final class BrushLogic {
    private BrushLogic() {
    }

    public static BlockState getParticleState(BlockHitResult hitResult, BlockState state) {
        if (hitResult.getDirection() != Direction.UP) {
            return state;
        }

        if (state.getBlock() instanceof FilledPrimitiveCauldronBlock cauldronBlock) {
            return cauldronBlock.getParticleBlock().defaultBlockState();
        }

        return state;
    }

    public static void onUseTick(
            ServerLevel serverLevel,
            LivingEntity livingEntity,
            ItemStack stack,
            BlockHitResult blockHitResult
    ) {
        if (blockHitResult.getDirection() != Direction.UP) {
            return;
        }

        BlockPos pos = blockHitResult.getBlockPos();

        if (tryFillCauldron(serverLevel, livingEntity, pos)) {
            return;
        }

        tryBrushCauldron(serverLevel, livingEntity, stack, pos);
    }

    private static boolean tryFillCauldron(ServerLevel serverLevel, LivingEntity livingEntity, BlockPos pos) {
        BlockState state = serverLevel.getBlockState(pos);

        if (!(state.getBlock() instanceof PrimitiveCauldronBlock primitiveCauldronBlock)) {
            return false;
        }

        InteractionHand oppositeInteractionHand = getOppositeInteractionHand(livingEntity);

        Optional<InteractionResult> optionalInteractionResult = primitiveCauldronBlock.attemptInteract(
                state,
                serverLevel,
                pos,
                livingEntity,
                oppositeInteractionHand
        );

        if (optionalInteractionResult.map(InteractionResult::consumesAction).orElse(false)) {
            livingEntity.swing(oppositeInteractionHand);
            return true;
        }

        return false;
    }

    private static void tryBrushCauldron(
            ServerLevel serverLevel,
            LivingEntity livingEntity,
            ItemStack stack,
            BlockPos pos
    ) {
        BlockEntity blockEntity = serverLevel.getBlockEntity(pos);

        if (!(blockEntity instanceof FilledPrimitiveCauldronBlockEntity filledPrimitiveCauldronBlockEntity)) {
            return;
        }

        if (!filledPrimitiveCauldronBlockEntity.brush(serverLevel, livingEntity, stack)) {
            return;
        }

        stack.hurtAndBreak(1, livingEntity, getInteractionHand(livingEntity));
    }

    private static InteractionHand getOppositeInteractionHand(LivingEntity livingEntity) {
        return getInteractionHand(livingEntity) == InteractionHand.MAIN_HAND
                ? InteractionHand.OFF_HAND
                : InteractionHand.MAIN_HAND;
    }

    private static InteractionHand getInteractionHand(LivingEntity livingEntity) {
        return livingEntity.getUsedItemHand();
    }
}
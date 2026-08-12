package dev.mariany.genesis.logic;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.block.custom.cauldron.FilledPrimitiveCauldronBlock;
import dev.mariany.genesis.block.custom.cauldron.PrimitiveCauldronBlock;
import dev.mariany.genesis.block.entity.custom.FilledPrimitiveCauldronBlockEntity;
import dev.mariany.genesis.event.item.BrushEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Optional;

public final class BrushLogic {
    private BrushLogic() {
    }

    public static void bootstrap() {
        Genesis.bootstrapLog("Brush Logic");
        BrushEvents.MODIFY_PARTICLE_STATE.register(BrushLogic::getParticleState);
        BrushEvents.USE_TICK.register(BrushLogic::onUseTick);
    }

    private static BlockState getParticleState(BlockHitResult hitResult, BlockState state) {
        if (hitResult.getDirection() != Direction.UP) {
            return state;
        }

        if (state.getBlock() instanceof FilledPrimitiveCauldronBlock cauldronBlock) {
            return cauldronBlock.getParticleBlock().defaultBlockState();
        }

        return state;
    }

    private static void onUseTick(
            Level level,
            LivingEntity livingEntity,
            ItemStack stack,
            HitResult hitResult
    ) {
        if (!(level instanceof ServerLevel serverLevel) || !(hitResult instanceof BlockHitResult blockHitResult)) {
            return;
        }

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

        if (!filledPrimitiveCauldronBlockEntity.sift(serverLevel, livingEntity, stack)) {
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

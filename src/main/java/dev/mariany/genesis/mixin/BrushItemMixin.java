package dev.mariany.genesis.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.genesis.block.custom.cauldron.FilledPrimitiveCauldronBlock;
import dev.mariany.genesis.block.custom.cauldron.PrimitiveCauldronBlock;
import dev.mariany.genesis.block.entity.custom.FilledPrimitiveCauldronBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(BrushItem.class)
public abstract class BrushItemMixin {
    @Shadow
    protected abstract HitResult calculateHitResult(Player user);

    @WrapOperation(
            method = "onUseTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/BrushItem;spawnDustParticles(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/entity/HumanoidArm;)V"
            )
    )
    public void wrapAddDustParticles(
            BrushItem brushItem,
            Level level,
            BlockHitResult hitResult,
            BlockState state,
            Vec3 userRotation,
            HumanoidArm arm,
            Operation<Void> original
    ) {
        boolean topSide = hitResult.getDirection() == Direction.UP;

        if (topSide && state.getBlock() instanceof FilledPrimitiveCauldronBlock filledPrimitiveCauldronBlock) {
            BlockState containingBlock = filledPrimitiveCauldronBlock.getParticleBlock().defaultBlockState();
            original.call(brushItem, level, hitResult, containingBlock, userRotation, arm);
        } else {
            original.call(brushItem, level, hitResult, state, userRotation, arm);
        }
    }

    @Inject(
            method = "onUseTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"
            )
    )
    private void injectUsageTick(
            Level level,
            LivingEntity livingEntity,
            ItemStack stack,
            int remainingUseTicks,
            CallbackInfo ci
    ) {
        if (!(livingEntity instanceof Player player)) {
            return;
        }

        HitResult hitResult = this.calculateHitResult(player);

        if (!(hitResult instanceof BlockHitResult blockHitResult)) {
            return;
        }

        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        if (blockHitResult.getDirection() != Direction.UP) {
            return;
        }

        BlockPos pos = blockHitResult.getBlockPos();

        BlockState state = serverLevel.getBlockState(pos);

        ItemStack offhandStack = player.getItemBySlot(EquipmentSlot.OFFHAND);

        InteractionHand interactionHand = ItemStack.matches(stack, offhandStack)
                ? InteractionHand.OFF_HAND
                : InteractionHand.MAIN_HAND;

        if (state.getBlock() instanceof PrimitiveCauldronBlock primitiveCauldronBlock) {
            InteractionHand oppositeInteractionHand = interactionHand == InteractionHand.MAIN_HAND ?
                    InteractionHand.OFF_HAND :
                    InteractionHand.MAIN_HAND;

            Optional<InteractionResult> optionalInteractionResult = primitiveCauldronBlock.attemptInteract(
                    state,
                    serverLevel,
                    pos,
                    player,
                    oppositeInteractionHand
            );

            if (optionalInteractionResult.map(InteractionResult::consumesAction).orElse(false)) {
                livingEntity.releaseUsingItem();
                player.swing(oppositeInteractionHand);
                return;
            }
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (!(blockEntity instanceof FilledPrimitiveCauldronBlockEntity cauldron)) {
            return;
        }

        if (!cauldron.brush(serverLevel, player, stack)) {
            return;
        }

        stack.hurtAndBreak(1, player, interactionHand);
        livingEntity.releaseUsingItem();
    }
}

package dev.mariany.genesis.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.genesis.block.custom.cauldron.FilledPrimitiveCauldronBlock;
import dev.mariany.genesis.block.entity.custom.FilledPrimitiveCauldronBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
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

@Mixin(BrushItem.class)
public abstract class BrushItemMixin {
    @Shadow
    protected abstract HitResult calculateHitResult(Player user);

    @WrapOperation(
            method = "onUseTick", at = @At(
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
            LivingEntity user,
            ItemStack stack,
            int remainingUseTicks,
            CallbackInfo ci
    ) {
        if (user instanceof Player playerEntity) {
            HitResult hitResult = this.calculateHitResult(playerEntity);

            if (hitResult instanceof BlockHitResult blockHitResult) {
                BlockPos blockPos = blockHitResult.getBlockPos();

                if (level instanceof ServerLevel serverLevel) {
                    BlockEntity blockEntity = level.getBlockEntity(blockPos);

                    if (blockEntity instanceof FilledPrimitiveCauldronBlockEntity filledPrimitiveCauldronBlockEntity) {
                        if (blockHitResult.getDirection() == Direction.UP) {
                            if (filledPrimitiveCauldronBlockEntity.brush(serverLevel, playerEntity, stack)) {
                                ItemStack offhandStack = playerEntity.getItemBySlot(EquipmentSlot.OFFHAND);
                                EquipmentSlot equipmentSlot = stack.equals(offhandStack) ?
                                        EquipmentSlot.OFFHAND :
                                        EquipmentSlot.MAINHAND;

                                stack.hurtAndBreak(1, playerEntity, equipmentSlot);
                                user.releaseUsingItem();
                            }
                        }
                    }
                }
            }
        }
    }
}

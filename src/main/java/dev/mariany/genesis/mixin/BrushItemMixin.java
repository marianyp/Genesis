package dev.mariany.genesis.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.genesis.block.custom.cauldron.FilledPrimitiveCauldronBlock;
import dev.mariany.genesis.block.entity.custom.FilledPrimitiveCauldronBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BrushItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Arm;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrushItem.class)
public abstract class BrushItemMixin {
    @Shadow
    protected abstract HitResult getHitResult(PlayerEntity user);

    @WrapOperation(method = "usageTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BrushItem;addDustParticles(Lnet/minecraft/world/World;Lnet/minecraft/util/hit/BlockHitResult;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/Arm;)V"))
    public void wrapAddDustParticles(BrushItem brushItem, World world, BlockHitResult hitResult, BlockState state, Vec3d userRotation, Arm arm, Operation<Void> original) {
        if (hitResult.getSide() == Direction.UP && state.getBlock() instanceof FilledPrimitiveCauldronBlock filledPrimitiveCauldronBlock) {
            BlockState containingBlock = filledPrimitiveCauldronBlock.getParticleBlock().getDefaultState();
            original.call(brushItem, world, hitResult, containingBlock, userRotation, arm);
        } else {
            original.call(brushItem, world, hitResult, state, userRotation, arm);
        }
    }

    @Inject(
            method = "usageTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getBlockEntity(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/entity/BlockEntity;"
            )
    )
    private void injectUsageTick(
            World world, LivingEntity user, ItemStack stack, int remainingUseTicks, CallbackInfo ci
    ) {
        if (user instanceof PlayerEntity playerEntity) {
            HitResult hitResult = this.getHitResult(playerEntity);

            if (hitResult instanceof BlockHitResult blockHitResult) {
                BlockPos blockPos = blockHitResult.getBlockPos();

                if (world instanceof ServerWorld serverWorld && world.getBlockEntity(blockPos) instanceof FilledPrimitiveCauldronBlockEntity filledPrimitiveCauldronBlockEntity) {
                    if (blockHitResult.getSide() == Direction.UP) {
                        if (filledPrimitiveCauldronBlockEntity.brush(serverWorld, playerEntity, stack)) {
                            ItemStack offhandStack = playerEntity.getEquippedStack(EquipmentSlot.OFFHAND);
                            EquipmentSlot equipmentSlot = stack.equals(offhandStack) ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND;
                            stack.damage(1, playerEntity, equipmentSlot);
                            user.stopUsingItem();
                        }
                    }
                }
            }
        }
    }
}

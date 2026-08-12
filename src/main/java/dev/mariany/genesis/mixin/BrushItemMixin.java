package dev.mariany.genesis.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.genesis.event.item.BrushEvents;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
            method = "onUseTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/BrushItem;spawnDustParticles(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/entity/HumanoidArm;)V"
            )
    )
    public void wrapSpawnDustParticles(
            BrushItem brushItem,
            Level level,
            BlockHitResult hitResult,
            BlockState state,
            Vec3 userRotation,
            HumanoidArm arm,
            Operation<Void> original
    ) {
        BlockState particleState = BrushEvents.MODIFY_PARTICLE_STATE.invoker().modify(hitResult, state);
        original.call(brushItem, level, hitResult, particleState, userRotation, arm);
    }

    @Inject(
            method = "onUseTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"
            )
    )
    private void injectOnUseTick(
            Level level,
            LivingEntity livingEntity,
            ItemStack stack,
            int remainingUseTicks,
            CallbackInfo ci
    ) {
        if (!(livingEntity instanceof Player player)) {
            return;
        }

        BrushEvents.USE_TICK.invoker().onUseTick(level, player, stack, this.calculateHitResult(player));
    }
}

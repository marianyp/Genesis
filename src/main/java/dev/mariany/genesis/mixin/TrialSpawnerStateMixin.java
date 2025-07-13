package dev.mariany.genesis.mixin;

import dev.mariany.genesis.advancement.criterion.GenesisCriteria;
import dev.mariany.genesis.mixin.accessor.TrialSpawnerDataAccessor;
import net.minecraft.block.enums.TrialSpawnerState;
import net.minecraft.block.spawner.TrialSpawnerData;
import net.minecraft.block.spawner.TrialSpawnerLogic;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

@Mixin(TrialSpawnerState.class)
public class TrialSpawnerStateMixin {
    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Set;remove(Ljava/lang/Object;)Z"
            )
    )
    private void onEjectReward(
            BlockPos pos, TrialSpawnerLogic logic, ServerWorld world, CallbackInfoReturnable<TrialSpawnerState> cir
    ) {
        TrialSpawnerData data = logic.getData();

        Set<UUID> players = ((TrialSpawnerDataAccessor) data).genesis$players();
        Iterator<UUID> iterator = players.iterator();

        if (iterator.hasNext()) {
            UUID uuid = iterator.next();
            boolean ominous = logic.isOminous();

            PlayerEntity player = world.getPlayerByUuid(uuid);

            if (player instanceof ServerPlayerEntity serverPlayer) {
                GenesisCriteria.COMPLETE_TRIAL_SPAWNER_ADVANCEMENT.trigger(serverPlayer, ominous);
            }
        }
    }
}

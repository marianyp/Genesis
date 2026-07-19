package dev.mariany.genesis.mixin;

import dev.mariany.genesis.logic.TirednessLogic;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Shadow
    @Final
    private List<ServerPlayer> players;

    @Inject(method = "wakeUpAllPlayers", at = @At(value = "HEAD"))
    private void injectWakeUpAllPlayers(CallbackInfo ci) {
        this.players
                .stream()
                .filter(LivingEntity::isSleeping)
                .forEach(TirednessLogic::onPlayerWake);
    }
}

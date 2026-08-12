package dev.mariany.genesis.mixin;

import dev.mariany.genesis.event.level.ServerLevelEvents;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Inject(method = "wakeUpAllPlayers", at = @At(value = "HEAD"))
    private void injectWakeUpAllPlayers(CallbackInfo ci) {
        ServerLevel serverLevel = (ServerLevel) (Object) this;
        ServerLevelEvents.BEFORE_AWAKEN_PLAYERS.invoker().beforeWakeUpAllPlayers(serverLevel);
    }
}

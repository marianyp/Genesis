package dev.mariany.genesis.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.genesis.advancement.criterion.GenesisCriteria;
import dev.mariany.genesis.age.AgeEntry;
import dev.mariany.genesis.age.AgeManager;
import dev.mariany.genesis.packet.clientbound.UpdateAgeUnlocksPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {
    @Shadow
    private ServerPlayerEntity owner;

    @WrapOperation(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/PlayerAdvancementTracker;onStatusUpdate(Lnet/minecraft/advancement/AdvancementEntry;)V"))
    public void wrapOnStatusUpdate(PlayerAdvancementTracker instance, AdvancementEntry advancement, Operation<Void> original) {
        GenesisCriteria.OBTAIN_ADVANCEMENT.trigger(owner, advancement);

        AgeManager ageManager = AgeManager.getInstance();
        List<AgeEntry> ages = ageManager.find(advancement);

        if (!ages.isEmpty()) {
            ServerPlayNetworking.send(owner, new UpdateAgeUnlocksPayload(ageManager.getAllUnlocks(owner)));
        }

        original.call(instance, advancement);
    }
}

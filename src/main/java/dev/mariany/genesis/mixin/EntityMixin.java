package dev.mariany.genesis.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.genesis.age.AgeEntry;
import dev.mariany.genesis.age.AgeHelpers;
import dev.mariany.genesis.age.AgeManager;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.dimension.PortalManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;


@Mixin(Entity.class)
public class EntityMixin {
    @WrapOperation(
            method = "tickPortalTeleportation",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/dimension/PortalManager;createTeleportTarget(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;)Lnet/minecraft/world/TeleportTarget;")
    )
    protected TeleportTarget tickPortalTeleportation(PortalManager portalManager, ServerWorld world, Entity entity, Operation<TeleportTarget> original) {
        if (entity instanceof ServerPlayerEntity serverPlayer) {
            AgeManager ageManager = AgeManager.getInstance();
            RegistryKey<World> worldRegistryKey = world.getRegistryKey();

            if (!AgeManager.getInstance().isUnlocked(serverPlayer, worldRegistryKey)) {
                Optional<AgeEntry> optionalAgeEntry = ageManager.getRequiredAges(worldRegistryKey)
                        .stream()
                        .findAny();

                optionalAgeEntry.ifPresent(ageEntry ->
                        AgeHelpers.notifyAgeLocked("tutorial.ageLocked.dimension", ageEntry.getAge(), serverPlayer)
                );

                return null;
            }
        }

        return original.call(portalManager, world, entity);
    }
}

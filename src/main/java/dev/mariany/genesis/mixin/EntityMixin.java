package dev.mariany.genesis.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.genesis.age.AgeEntry;
import dev.mariany.genesis.age.AgeLockNotifier;
import dev.mariany.genesis.age.AgeManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.Tameable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.dimension.PortalManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;


@Mixin(Entity.class)
public class EntityMixin {
    @WrapOperation(
            method = "tickPortalTeleportation",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/dimension/PortalManager;createTeleportTarget(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/Entity;)Lnet/minecraft/world/TeleportTarget;"
            )
    )
    protected TeleportTarget wrapCreateTeleportTarget(PortalManager portalManager, ServerWorld world, Entity entity, Operation<TeleportTarget> original) {
        TeleportTarget target = original.call(portalManager, world, entity);

        if (target != null) {
            boolean notify = entity instanceof ServerPlayerEntity;
            Optional<ServerPlayerEntity> optionalPlayer = getPlayerForPortalCheck(entity);

            if (optionalPlayer.isPresent()) {
                AgeManager ageManager = AgeManager.getInstance();
                ServerPlayerEntity player = optionalPlayer.get();
                RegistryKey<World> worldRegistryKey = target.world().getRegistryKey();

                if (!ageManager.isUnlocked(player, worldRegistryKey)) {
                    Optional<AgeEntry> optionalAgeEntry = ageManager.getRequiredAges(worldRegistryKey)
                            .stream()
                            .findAny();

                    if (notify) {
                        optionalAgeEntry.ifPresent(ageEntry ->
                                AgeLockNotifier.notifyAgeLocked(
                                        "tutorial.ageLocked.dimension",
                                        ageEntry.getAge(),
                                        player
                                )
                        );
                    }

                    return null;
                }
            }
        }

        return target;
    }

    @Unique
    private Optional<ServerPlayerEntity> getPlayerForPortalCheck(Entity entity) {
        ServerPlayerEntity player = null;

        if (entity instanceof ServerPlayerEntity serverPlayer) {
            player = serverPlayer;
        } else if (entity instanceof Ownable ownable && ownable.getOwner() instanceof ServerPlayerEntity serverPlayer) {
            player = serverPlayer;
        } else if (
                entity instanceof Tameable tameable && tameable.getOwner() instanceof ServerPlayerEntity serverPlayer
        ) {
            player = serverPlayer;
        }

        return Optional.ofNullable(player);
    }
}

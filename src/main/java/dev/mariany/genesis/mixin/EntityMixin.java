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
    protected TeleportTarget wrapCreateTeleportTarget(PortalManager portalManager, ServerWorld world, Entity entity, Operation<TeleportTarget> original) {
        TeleportTarget target = original.call(portalManager, world, entity);

        if (target != null) {
            boolean notify = false;
            ServerPlayerEntity player;

            switch (entity) {
                case ServerPlayerEntity serverPlayer -> {
                    player = serverPlayer;
                    notify = true;
                }
                case Ownable ownable when ownable.getOwner() instanceof ServerPlayerEntity serverPlayer ->
                        player = serverPlayer;
                case Tameable tameable when tameable.getOwner() instanceof ServerPlayerEntity serverPlayer ->
                        player = serverPlayer;
                case null, default -> {
                    return target;
                }
            }

            AgeManager ageManager = AgeManager.getInstance();
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

        return target;
    }
}

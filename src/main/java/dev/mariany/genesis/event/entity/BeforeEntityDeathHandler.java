package dev.mariany.genesis.event.entity;

import dev.mariany.genesis.advancement.criterion.GenesisCriteria;
import dev.mariany.genesis.config.ConfigHandler;
import dev.mariany.genesis.config.GenesisConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;

public class BeforeEntityDeathHandler {
    public static void onEntityDeath(LivingEntity livingEntity) {
        GenesisConfig config = ConfigHandler.getConfig();

        if (livingEntity instanceof ElderGuardianEntity elderGuardian) {
            if (elderGuardian.getEntityWorld() instanceof ServerWorld serverWorld) {
                List<Entity> otherEntities = serverWorld.getOtherEntities(
                        null,
                        Box.from(elderGuardian.getEntityPos()).expand(config.oceanMonumentSearchRadius),
                        Entity::isAlive
                );

                List<ElderGuardianEntity> elders = new ArrayList<>();
                List<ServerPlayerEntity> players = new ArrayList<>();

                for (Entity entity : otherEntities) {
                    if (entity instanceof ElderGuardianEntity otherElder) {
                        elders.add(otherElder);
                    }

                    if (entity instanceof ServerPlayerEntity serverPlayer) {
                        players.add(serverPlayer);
                    }
                }

                if (elders.isEmpty()) {
                    players.forEach(GenesisCriteria.COMPLETE_MONUMENT::trigger);
                } else if (config.highlightOtherElderGuardians) {
                    StatusEffectInstance statusEffect = new StatusEffectInstance(StatusEffects.GLOWING, 200);
                    elders.forEach(elder -> elder.addStatusEffect(statusEffect));
                }
            }
        }
    }
}

package dev.mariany.genesis.logic;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.advancement.criterion.GenesisCriteria;
import dev.mariany.genesis.event.entity.EntityEvents;
import dev.mariany.genesis.sound.GenesisSoundEvents;
import dev.mariany.genesis.world.level.gamerules.GenesisGameRules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public final class OceanMonumentLogic {
    private OceanMonumentLogic() {
    }

    public static void bootstrap() {
        Genesis.bootstrapLog("Ocean Monument Logic");
        EntityEvents.BEFORE_ENTITY_DEATH.register(OceanMonumentLogic::onEntityDeath);
    }

    private static void onEntityDeath(LivingEntity livingEntity) {
        if (!(livingEntity instanceof ElderGuardian elderGuardian)) {
            return;
        }

        if (!(elderGuardian.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        GameRules gameRules = serverLevel.getGameRules();

        double oceanMonumentRadius = gameRules.get(GenesisGameRules.OCEAN_MONUMENT_RADIUS);

        List<Entity> otherEntities = serverLevel.getEntities(
                (Entity) null,
                AABB.unitCubeFromLowerCorner(elderGuardian.position()).inflate(oceanMonumentRadius),
                Entity::isAlive
        );

        List<ElderGuardian> elders = new ArrayList<>();
        List<ServerPlayer> players = new ArrayList<>();

        for (Entity entity : otherEntities) {
            if (entity instanceof ElderGuardian otherElder) {
                elders.add(otherElder);
            }

            if (entity instanceof ServerPlayer serverPlayer) {
                players.add(serverPlayer);
            }
        }

        if (elders.isEmpty()) {
            players.forEach(GenesisCriteria.COMPLETE_MONUMENT::trigger);
            return;
        }

        boolean revealElderGuardians = gameRules.get(GenesisGameRules.REVEAL_ELDER_GUARDIANS);

        if (!revealElderGuardians) {
            return;
        }

        MobEffectInstance statusEffect = new MobEffectInstance(MobEffects.GLOWING, 300);
        elders.forEach(elder -> elder.addEffect(statusEffect));

        serverLevel.playSound(
                null,
                livingEntity.blockPosition(),
                GenesisSoundEvents.ENTITY_ELDER_GUARDIAN_REVEAL,
                SoundSource.NEUTRAL
        );
    }
}

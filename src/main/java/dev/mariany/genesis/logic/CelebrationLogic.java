package dev.mariany.genesis.logic;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.attachment.GenesisAttachmentTypes;
import dev.mariany.genesis.world.level.saveddata.CelebrationState;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.MoveToSkySeeingSpot;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Level;

import java.util.*;

public class CelebrationLogic {
    private static final int WHITE = 0xFFFFFF;
    private static final int RED = 0xFF2626;
    private static final int ORANGE = 0xFF8C0D;
    private static final int YELLOW = 0xFFE61A;
    private static final int BLUE = 0x2699FF;

    private static final List<Integer> COLORS = List.of(WHITE, RED, ORANGE, YELLOW, BLUE);

    private static final int BURST_DELAY = 20;
    private static final int DELAY_GROWTH = 6;
    private static final double DELAY_GROWTH_RATE = 0.4;
    private static final int CYCLES = 3;
    private static final int FINAL_STAGE_OFFSET = 10;

    private static final int[] LAUNCH_TICKS = createLaunchTicks();
    private static final int CELEBRATION_DURATION_TICKS = LAUNCH_TICKS[LAUNCH_TICKS.length - 1] + FINAL_STAGE_OFFSET;

    private CelebrationState state;

    private static int[] createLaunchTicks() {
        int[] ticks = new int[CYCLES * COLORS.size()];

        int nextTick = 0;

        for (int index = 0; index < ticks.length; index++) {
            ticks[index] = nextTick;

            double additionalDelay = DELAY_GROWTH * (1 - Math.exp(-DELAY_GROWTH_RATE * (index + 1)));

            nextTick += (int) Math.round(BURST_DELAY + additionalDelay);
        }

        return ticks;
    }

    public void bootstrap() {
        Genesis.bootstrapLog("Celebration Logic");
        ServerLivingEntityEvents.AFTER_DAMAGE.register(this::onEntityDamaged);
        ServerLivingEntityEvents.AFTER_DEATH.register(this::onEntityDeath);
        ServerEntityEvents.ENTITY_UNLOAD.register(this::onEntityUnload);
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.SERVER_STOPPED.register(this::onServerStopped);
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
    }

    private void onEntityDamaged(
            LivingEntity entity,
            DamageSource source,
            float baseDamageTaken,
            float damageTaken,
            boolean blocked
    ) {
        addParticipant(entity, source);
    }

    private void onEntityDeath(LivingEntity entity, DamageSource source) {
        if (!(entity instanceof EnderDragon enderDragon) || !Level.END.equals(entity.level().dimension())) {
            return;
        }

        this.addParticipant(entity, source);
        this.restartCelebration(enderDragon);
    }

    private void addParticipant(LivingEntity entity, DamageSource source) {
        if (!(entity instanceof EnderDragon enderDragon) || !Level.END.equals(entity.level().dimension())) {
            return;
        }

        if (!(source.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        List<UUID> participants = enderDragon.getAttachedOrElse(
                GenesisAttachmentTypes.CELEBRATION_PARTICIPANTS,
                List.of()
        );

        UUID playerUUID = player.getUUID();

        if (participants.contains(playerUUID)) {
            return;
        }

        List<UUID> updatedParticipants = new ArrayList<>(participants);
        updatedParticipants.add(playerUUID);

        enderDragon.setAttached(GenesisAttachmentTypes.CELEBRATION_PARTICIPANTS, List.copyOf(updatedParticipants));
    }

    private void onEntityUnload(Entity entity, ServerLevel level) {
        if (!Entity.RemovalReason.KILLED.equals(entity.getRemovalReason())) {
            return;
        }

        if (!(entity instanceof EnderDragon enderDragon) || !Level.END.equals(level.dimension())) {
            return;
        }

        this.restartCelebration(enderDragon);

        this.getState(level.getServer()).stopTracking(enderDragon);
    }

    private void restartCelebration(EnderDragon enderDragon) {
        MinecraftServer server = enderDragon.level().getServer();

        if (server == null) {
            return;
        }

        CelebrationState state = this.getState(server);

        if (state.isTracking(enderDragon)) {
            return;
        }

        List<UUID> participants = enderDragon.getAttachedOrElse(
                GenesisAttachmentTypes.CELEBRATION_PARTICIPANTS,
                List.of()
        );

        if (participants.isEmpty()) {
            return;
        }

        enderDragon.setAttached(GenesisAttachmentTypes.CELEBRATION_PARTICIPANTS, List.of());

        state.restart(participants, enderDragon);
    }

    private CelebrationState getState(MinecraftServer server) {
        if (this.state == null) {
            this.state = CelebrationState.get(server);
        }

        return this.state;
    }

    private void onServerStarted(MinecraftServer server) {
        this.state = CelebrationState.get(server);
    }

    private void onServerStopped(MinecraftServer server) {
        this.state = null;
    }

    private void onServerTick(MinecraftServer server) {
        CelebrationState state = this.getState(server);

        List<UUID> participants = state.getParticipants();

        if (participants.isEmpty()) {
            return;
        }

        if (state.getTickProgress() >= CELEBRATION_DURATION_TICKS) {
            state.stop();
            return;
        }

        int launchIndex = getLaunchIndex(state.getTickProgress());

        if (launchIndex >= 0) {
            PlayerList playerList = server.getPlayerList();

            for (UUID participant : participants) {
                ServerPlayer player = playerList.getPlayer(participant);

                if (player != null && Level.END.equals(player.level().dimension())) {
                    trySpawnFirework(player, launchIndex);
                }
            }
        }

        state.advance();
    }

    private static int getLaunchIndex(int tick) {
        for (int index = 0; index < LAUNCH_TICKS.length; index++) {
            if (LAUNCH_TICKS[index] == tick) {
                return index;
            }
        }

        return -1;
    }

    private static void trySpawnFirework(ServerPlayer player, int launchIndex) {
        ServerLevel level = player.level();

        if (!MoveToSkySeeingSpot.hasNoBlocksAbove(level, player, player.blockPosition())) {
            return;
        }

        ItemStack firework = createFirework(colorFor(player, launchIndex));

        Projectile.spawnProjectile(
                new FireworkRocketEntity(
                        level,
                        player,
                        player.getX(),
                        player.getEyeY(),
                        player.getZ(),
                        firework
                ),
                level,
                firework
        );
    }

    private static ItemStack createFirework(int color) {
        ItemStack rocket = new ItemStack(Items.FIREWORK_ROCKET);

        rocket.set(
                DataComponents.FIREWORKS,
                new Fireworks(
                        (byte) 0,
                        List.of(
                                new FireworkExplosion(
                                        FireworkExplosion.Shape.BURST,
                                        IntList.of(color),
                                        IntList.of(),
                                        false,
                                        true
                                )
                        )
                )
        );

        return rocket;
    }

    private static int colorFor(ServerPlayer player, int launchIndex) {
        int cycle = launchIndex / COLORS.size();
        UUID playerId = player.getUUID();

        long seed = player.level().getSeed()
                ^ playerId.getMostSignificantBits()
                ^ playerId.getLeastSignificantBits()
                ^ ((long) cycle * 0x9E3779B97F4A7C15L);

        RandomSource random = RandomSource.create(seed);
        List<Integer> shuffledColors = new ArrayList<>(COLORS);

        for (int index = shuffledColors.size() - 1; index > 0; index--) {
            Collections.swap(shuffledColors, index, random.nextInt(index + 1));
        }

        return shuffledColors.get(launchIndex % COLORS.size());
    }
}

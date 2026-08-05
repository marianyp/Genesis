package dev.mariany.genesis.logic;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.attachment.GenesisAttachmentTypes;
import dev.mariany.genesis.packet.clientbound.UpdateTirednessLogicPayload;
import dev.mariany.genesis.world.level.gamerules.GenesisGameRules;
import dev.mariany.genesis.world.level.gamerules.SyncedGameRule;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gamerules.GameRules;
import org.jetbrains.annotations.Nullable;

public class TirednessLogic {
    private static final int TICKS_PER_SECOND = 20;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MINUTES_PER_DAY = 20;
    private static final int TICKS_PER_DAY = TICKS_PER_SECOND * SECONDS_PER_MINUTE * MINUTES_PER_DAY;

    private static final Player.BedSleepingProblem BED_SLEEPING_PROBLEM = new Player.BedSleepingProblem(
            Component.translatable("block.minecraft.bed.genesis.tiredness")
    );

    private final SyncedGameRule<Double> syncedMinimumDaysBeforeSleeping = new SyncedGameRule<>(
            GenesisGameRules.MINIMUM_DAYS_BEFORE_SLEEPING,
            0D,
            UpdateTirednessLogicPayload::new
    );

    public void setMinimumDaysBeforeSleeping(double days) {
        this.syncedMinimumDaysBeforeSleeping.setValue(days);
    }

    public static void onPlayerWake(Player player) {
        resetPlayerTiredness(player);
    }

    public void bootstrap() {
        Genesis.bootstrapLog("Tiredness Logic");
        this.syncedMinimumDaysBeforeSleeping.bootstrap();
        ServerPlayerEvents.AFTER_RESPAWN.register(TirednessLogic::onPlayerRespawn);
        ServerTickEvents.END_SERVER_TICK.register(this::onEndServerTick);
        EntitySleepEvents.ALLOW_SLEEPING.register(this::onAllowSleeping);
    }

    private static void onPlayerRespawn(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        if (alive) {
            return;
        }

        ServerLevel serverLevel = newPlayer.level();
        GameRules gameRules = serverLevel.getGameRules();

        if (!gameRules.get(GenesisGameRules.RESPAWN_RESETS_TIREDNESS)) {
            return;
        }

        resetPlayerTiredness(oldPlayer);
    }

    private static void resetPlayerTiredness(Player player) {
        player.setAttached(GenesisAttachmentTypes.AWAKE_TICKS, 0);
    }

    private void onEndServerTick(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            onPlayerTick(player);
        }
    }

    private void onPlayerTick(ServerPlayer player) {
        if (player.isSpectator() || !player.isAlive()) {
            return;
        }

        this.updateAwakeTicks(player);
    }

    private void updateAwakeTicks(ServerPlayer player) {
        int awakeTicks = getAwakeTicks(player);
        int minimumTicksBeforeSleeping = this.getMinimumTicksBeforeSleeping();
        int updatedAwakeTicks = Math.min(awakeTicks + 1, minimumTicksBeforeSleeping);
        player.setAttached(GenesisAttachmentTypes.AWAKE_TICKS, updatedAwakeTicks);
    }

    @Nullable
    private Player.BedSleepingProblem onAllowSleeping(Player player, BlockPos pos) {
        return canSleep(player) ? null : TirednessLogic.BED_SLEEPING_PROBLEM;
    }

    private boolean canSleep(Player player) {
        return player.isCreative() || getAwakeTicks(player) >= this.getMinimumTicksBeforeSleeping();
    }

    private static int getAwakeTicks(Player player) {
        return player.getAttachedOrElse(GenesisAttachmentTypes.AWAKE_TICKS, 0);
    }

    public int getMinimumTicksBeforeSleeping() {
        return Mth.floor(this.syncedMinimumDaysBeforeSleeping.getValue() * TICKS_PER_DAY);
    }
}

package dev.mariany.genesis.logic;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.attachment.GenesisAttachmentTypes;
import dev.mariany.genesis.event.level.ServerLevelEvents;
import dev.mariany.genesis.packet.clientbound.UpdateTirednessLogicPayload;
import dev.mariany.genesis.world.level.gamerules.GameRuleState;
import dev.mariany.genesis.world.level.gamerules.GenesisGameRules;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
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

    private final GameRuleState<Double> tirednessRateState = new GameRuleState<>(
            () -> GenesisGameRules.TIREDNESS_RATE,
            this::updateMinimumTicksBeforeSleeping
    );

    private final GameRuleState<Integer> playersSleepingPercentageState = new GameRuleState<>(
            () -> GameRules.PLAYERS_SLEEPING_PERCENTAGE,
            this::updateMinimumTicksBeforeSleeping
    );

    private int minimumTicksBeforeSleeping = 0;

    public void setMinimumTicksBeforeSleeping(int minimumTicksBeforeSleeping) {
        this.minimumTicksBeforeSleeping = minimumTicksBeforeSleeping;
    }

    public void bootstrap() {
        Genesis.bootstrapLog("Tiredness Logic");

        this.tirednessRateState.bootstrap();
        this.playersSleepingPercentageState.bootstrap();

        this.minimumTicksBeforeSleeping = this.calculateMinimumTicksBeforeSleeping();

        ServerPlayConnectionEvents.JOIN.register(this::onPlayerJoin);
        ServerPlayerEvents.AFTER_RESPAWN.register(this::onRespawn);
        ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register(this::onChangeLevel);
        ServerTickEvents.END_SERVER_TICK.register(this::onEndServerTick);
        EntitySleepEvents.ALLOW_SLEEPING.register(this::onAllowSleeping);
        ServerLevelEvents.BEFORE_AWAKEN_PLAYERS.register(this::beforeAwakenPlayers);
    }

    private void onPlayerJoin(ServerGamePacketListenerImpl listener, PacketSender sender, MinecraftServer server) {
        this.syncTiredness(listener.player);
    }

    private void onRespawn(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        this.syncTiredness(newPlayer);
    }

    private void onChangeLevel(ServerPlayer serverPlayer, ServerLevel origin, ServerLevel destination) {
        this.syncTiredness(serverPlayer);
    }

    private void onEndServerTick(MinecraftServer server) {
        server.getPlayerList().getPlayers().forEach(this::onPlayerTick);
    }

    private void onPlayerTick(ServerPlayer player) {
        if (player.isSpectator()) {
            return;
        }

        this.updateAwakeTicks(player);
    }

    private void updateAwakeTicks(ServerPlayer serverPlayer) {
        int previousAwakeTicks = getAwakeTicks(serverPlayer);
        int minimumTicksBeforeSleeping = this.getMinimumTicksBeforeSleeping();

        ServerLevel serverLevel = serverPlayer.level();
        GameRules gameRules = serverLevel.getGameRules();

        int awakeTicks;

        if (serverPlayer.isAlive()) {
            awakeTicks = Math.min(previousAwakeTicks + 1, minimumTicksBeforeSleeping);
        } else if (gameRules.get(GenesisGameRules.RESPAWN_RESETS_TIREDNESS)) {
            awakeTicks = 0;
        } else {
            awakeTicks = previousAwakeTicks;
        }

        serverPlayer.setAttached(GenesisAttachmentTypes.AWAKE_TICKS, awakeTicks);

        int previousTirednessPercentage = getTirednessPercentage(previousAwakeTicks, minimumTicksBeforeSleeping);
        int tirednessPercentage = getTirednessPercentage(awakeTicks, minimumTicksBeforeSleeping);

        if (previousTirednessPercentage != tirednessPercentage) {
            this.syncTiredness(serverPlayer);
        }
    }

    private static int getTirednessPercentage(int awakeTicks, int minimumTicksBeforeSleeping) {
        if (minimumTicksBeforeSleeping <= 0) {
            return 100;
        }

        return Mth.clamp((int) ((long) awakeTicks * 100 / minimumTicksBeforeSleeping), 0, 100);
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
        return this.minimumTicksBeforeSleeping;
    }

    private void updateMinimumTicksBeforeSleeping(MinecraftServer server) {
        this.minimumTicksBeforeSleeping = this.calculateMinimumTicksBeforeSleeping();
        this.syncTiredness(server);
    }

    private int calculateMinimumTicksBeforeSleeping() {
        double tirednessRate = this.tirednessRateState.getValue();

        if (tirednessRate <= 0) {
            return 0;
        }

        double playersSleepingPercentage = this.playersSleepingPercentageState.getValue();
        double playersSleepingRatio = playersSleepingPercentage / 100D;

        double timeMultiplier = 3 - (playersSleepingRatio * 2);

        double minimumTicksBeforeSleeping = TICKS_PER_DAY * tirednessRate * timeMultiplier;

        if (minimumTicksBeforeSleeping >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }

        return Mth.ceil(minimumTicksBeforeSleeping);
    }

    private void syncTiredness(MinecraftServer server) {
        PlayerLookup.all(server).forEach(this::syncTiredness);
    }

    private void syncTiredness(ServerPlayer player) {
        ServerPlayNetworking.send(
                player,
                new UpdateTirednessLogicPayload(this.minimumTicksBeforeSleeping, getAwakeTicks(player))
        );
    }

    private void beforeAwakenPlayers(ServerLevel level) {
        level.players().stream().filter(LivingEntity::isSleeping).forEach(this::onPlayerWake);
    }

    private void onPlayerWake(Player player) {
        this.resetPlayerTiredness(player);
    }

    private void resetPlayerTiredness(Player player) {
        player.setAttached(GenesisAttachmentTypes.AWAKE_TICKS, 0);

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        this.syncTiredness(serverPlayer);
    }
}

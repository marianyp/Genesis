package dev.mariany.genesis.logic;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.attachment.GenesisAttachmentTypes;
import dev.mariany.genesis.event.level.ServerLevelEvents;
import dev.mariany.genesis.packet.clientbound.UpdateTirednessLogicPayload;
import dev.mariany.genesis.tag.GenesisTags;
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
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class TirednessLogic {
    private static final int TICKS_PER_SECOND = 20;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MINUTES_PER_DAY = 20;
    private static final int TICKS_PER_DAY = TICKS_PER_SECOND * SECONDS_PER_MINUTE * MINUTES_PER_DAY;
    private static final int FULL_REST_TIREDNESS_REDUCTION_PERCENTAGE = 100;
    private static final int PARTIAL_REST_TIREDNESS_REDUCTION_PERCENTAGE = 50;

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

    private void updateMinimumTicksBeforeSleeping(MinecraftServer server) {
        this.minimumTicksBeforeSleeping = this.calculateMinimumTicksBeforeSleeping();
        this.syncTiredness(server);
    }

    private void syncTiredness(MinecraftServer server) {
        PlayerLookup.all(server).forEach(this::syncTiredness);
    }

    public void setMinimumTicksBeforeSleeping(int minimumTicksBeforeSleeping) {
        this.minimumTicksBeforeSleeping = minimumTicksBeforeSleeping;
    }

    public void appendTooltip(ItemStack stack, List<Component> lines) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return;
        }

        BlockState bed = blockItem.getBlock().defaultBlockState();

        this.getTirednessReductionPercentage(bed)
            .flatMap(reductionPercentage -> this.createTirednessChangeTooltip(-reductionPercentage))
            .ifPresent(tirednessChangeTooltip -> {
                lines.add(Component.translatable("tiredness.genesis.when_slept_in").withStyle(ChatFormatting.GRAY));
                lines.add(tirednessChangeTooltip);
            });
    }

    private Optional<Component> createTirednessChangeTooltip(int changePercentage) {
        String translationKey;
        ChatFormatting formatting;

        if (changePercentage < 0) {
            translationKey = "tiredness.genesis.decrease";
            formatting = ChatFormatting.BLUE;
        } else if (changePercentage > 0) {
            translationKey = "tiredness.genesis.increase";
            formatting = ChatFormatting.RED;
        } else {
            return Optional.empty();
        }

        Component tirednessChange = Component.translatable(translationKey, Math.abs(changePercentage));

        return Optional.of(CommonComponents.space().append(tirednessChange).withStyle(formatting));
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
        EntitySleepEvents.START_SLEEPING.register(this::onStartSleeping);
        ServerLevelEvents.BEFORE_AWAKEN_PLAYERS.register(this::onAwakenPlayers);
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

    private void onPlayerJoin(ServerGamePacketListenerImpl listener, PacketSender sender, MinecraftServer server) {
        this.syncTiredness(listener.player);
    }

    private void onRespawn(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        this.syncTiredness(oldPlayer);
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

        if (previousTirednessPercentage == tirednessPercentage) {
            return;
        }

        this.syncTiredness(serverPlayer);
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

    public int getMinimumTicksBeforeSleeping() {
        return this.minimumTicksBeforeSleeping;
    }

    private void onStartSleeping(LivingEntity entity, BlockPos sleepingPos) {
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        BlockState bed = player.level().getBlockState(sleepingPos);

        player.setAttached(GenesisAttachmentTypes.BED, bed);
    }

    private void onAwakenPlayers(ServerLevel level) {
        level.players().stream().filter(LivingEntity::isSleeping).forEach(this::onPlayerWake);
    }

    private void onPlayerWake(Player player) {
        BlockState bed = player.getAttached(GenesisAttachmentTypes.BED);

        Integer tirednessReductionPercentage = bed == null ?
                null :
                this.getTirednessReductionPercentage(bed).orElse(null);

        int effectiveTirednessReductionPercentage = tirednessReductionPercentage == null
                ? FULL_REST_TIREDNESS_REDUCTION_PERCENTAGE
                : tirednessReductionPercentage;

        long awakeTickReduction = (long) this.minimumTicksBeforeSleeping
                * effectiveTirednessReductionPercentage
                / 100;

        long adjustedAwakeTicks = getAwakeTicks(player) - awakeTickReduction;
        int awakeTicks = (int) Math.max(0L, Math.min(adjustedAwakeTicks, this.minimumTicksBeforeSleeping));

        this.setAwakeTicks(player, awakeTicks);

        player.removeAttached(GenesisAttachmentTypes.BED);
    }

    private Optional<Integer> getTirednessReductionPercentage(BlockState bed) {
        if (bed.is(GenesisTags.Blocks.PARTIAL_REST_BEDS)) {
            return Optional.of(PARTIAL_REST_TIREDNESS_REDUCTION_PERCENTAGE);
        }

        if (bed.is(BlockTags.BEDS)) {
            return Optional.of(FULL_REST_TIREDNESS_REDUCTION_PERCENTAGE);
        }

        return Optional.empty();
    }

    private void setAwakeTicks(Player player, int awakeTicks) {
        player.setAttached(GenesisAttachmentTypes.AWAKE_TICKS, awakeTicks);

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        this.syncTiredness(serverPlayer);
    }

    private void syncTiredness(ServerPlayer player) {
        ServerPlayNetworking.send(
                player,
                new UpdateTirednessLogicPayload(this.minimumTicksBeforeSleeping, getAwakeTicks(player))
        );
    }

    private static int getAwakeTicks(Player player) {
        return player.getAttachedOrElse(GenesisAttachmentTypes.AWAKE_TICKS, 0);
    }
}

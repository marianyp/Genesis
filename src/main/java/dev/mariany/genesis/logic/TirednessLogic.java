package dev.mariany.genesis.logic;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.attachment.GenesisAttachmentTypes;
import dev.mariany.genesis.event.level.ServerLevelEvents;
import dev.mariany.genesis.packet.clientbound.UpdateTirednessLogicPayload;
import dev.mariany.genesis.tag.GenesisTags;
import dev.mariany.genesis.world.effect.GenesisMobEffects;
import dev.mariany.genesis.world.effect.PoorlyRestedMobEffect;
import dev.mariany.genesis.world.level.gamerules.GameRuleState;
import dev.mariany.genesis.world.level.gamerules.GenesisGameRules;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
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

    private static final int POOR_SLEEP_QUALITY_EFFECT_LEVEL = 1;
    private static final int VERY_POOR_SLEEP_QUALITY_EFFECT_LEVEL = 2;

    private static final Player.BedSleepingProblem TIREDNESS_SLEEPING_PROBLEM = new Player.BedSleepingProblem(
            Component.translatable("block.minecraft.bed.genesis.tiredness")
    );

    private static final Player.BedSleepingProblem POORLY_RESTED_SLEEPING_PROBLEM = new Player.BedSleepingProblem(
            Component.translatable("block.minecraft.bed.genesis.poorly_rested")
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

    public boolean isDisabled() {
        return this.minimumTicksBeforeSleeping <= 0;
    }

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

    public static void appendTooltip(ItemStack stack, List<Component> lines) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return;
        }

        BlockState bed = blockItem.getBlock().defaultBlockState();

        getSleepQualityTooltip(bed).ifPresent(lines::add);

        Optional<Float> optionalReductionFactor = getTirednessReductionFactor(bed);

        if (optionalReductionFactor.isEmpty()) {
            return;
        }

        int reductionPercentage = (int) (optionalReductionFactor.get() * 100);
        Optional<Component> optionalTirednessChangeTooltip = createTirednessChangeTooltip(-reductionPercentage);

        if (optionalTirednessChangeTooltip.isEmpty()) {
            return;
        }

        lines.add(CommonComponents.EMPTY);
        lines.add(Component.translatable("tiredness.genesis.when_slept_in").withStyle(ChatFormatting.GRAY));
        lines.add(optionalTirednessChangeTooltip.get());
    }

    private static Optional<Component> getSleepQualityTooltip(BlockState bed) {
        MutableComponent component;

        if (bed.is(GenesisTags.Blocks.VERY_POOR_SLEEP_QUALITY)) {
            component = Component.translatable("sleep_quality.genesis.very_poor");
        } else if (bed.is(GenesisTags.Blocks.POOR_SLEEP_QUALITY)) {
            component = Component.translatable("sleep_quality.genesis.poor");
        } else {
            return Optional.empty();
        }

        return Optional.of(component.withStyle(ChatFormatting.GRAY));
    }

    private static Optional<Component> createTirednessChangeTooltip(int changePercentage) {
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

        ServerPlayerEvents.JOIN.register(this::onPlayerJoin);
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

    private void onPlayerJoin(ServerPlayer player) {
        this.syncTiredness(player);
    }

    private void onRespawn(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        this.syncTiredness(oldPlayer);
    }

    private void onChangeLevel(ServerPlayer serverPlayer, ServerLevel origin, ServerLevel destination) {
        this.syncTiredness(serverPlayer);
    }

    private void onEndServerTick(MinecraftServer server) {
        if (this.isDisabled()) {
            return;
        }

        server.getPlayerList().getPlayers().forEach(this::onPlayerTick);
    }

    private void onPlayerTick(ServerPlayer player) {
        if (this.isDisabled() || player.isSpectator()) {
            return;
        }

        this.updateAwakeTicks(player);
    }

    private void updateAwakeTicks(ServerPlayer serverPlayer) {
        ServerLevel serverLevel = serverPlayer.level();
        GameRules gameRules = serverLevel.getGameRules();

        int minimumTicksBeforeSleeping = this.minimumTicksBeforeSleeping;
        int previousAwakeTicks = getAwakeTicks(serverPlayer);

        int awakeTicks;

        if (serverPlayer.isAlive()) {
            awakeTicks = previousAwakeTicks >= minimumTicksBeforeSleeping ? previousAwakeTicks : previousAwakeTicks + 1;
        } else if (previousAwakeTicks > 0 && gameRules.get(GenesisGameRules.RESPAWN_RESETS_TIREDNESS)) {
            awakeTicks = 0;
        } else {
            return;
        }

        int clampedAwakeTicks = Math.min(awakeTicks, minimumTicksBeforeSleeping);

        serverPlayer.setAttached(GenesisAttachmentTypes.AWAKE_TICKS, clampedAwakeTicks);

        if (clampedAwakeTicks == awakeTicks) {
            int previousTirednessPercentage = getTirednessPercentage(previousAwakeTicks);
            int tirednessPercentage = getTirednessPercentage(clampedAwakeTicks);

            if (previousTirednessPercentage == tirednessPercentage) {
                return;
            }
        }

        this.syncTiredness(serverPlayer);
    }

    public int getTirednessPercentage(int awakeTicks) {
        int minimumTicksBeforeSleeping = this.minimumTicksBeforeSleeping;

        if (minimumTicksBeforeSleeping <= 0) {
            return 100;
        }

        return Mth.clamp((int) ((long) awakeTicks * 100 / minimumTicksBeforeSleeping), 0, 100);
    }

    @Nullable
    private Player.BedSleepingProblem onAllowSleeping(Player player, BlockPos pos) {
        BlockState bed = player.level().getBlockState(pos);

        if (player.hasEffect(GenesisMobEffects.POORLY_RESTED) && isPoorSleepQuality(bed)) {
            return POORLY_RESTED_SLEEPING_PROBLEM;
        }

        return canSleep(player) ? null : TIREDNESS_SLEEPING_PROBLEM;
    }

    private static boolean isPoorSleepQuality(BlockState bed) {
        return bed.is(GenesisTags.Blocks.POOR_SLEEP_QUALITY) || bed.is(GenesisTags.Blocks.VERY_POOR_SLEEP_QUALITY);
    }

    private boolean canSleep(Player player) {
        return player.isCreative() || getAwakeTicks(player) >= this.minimumTicksBeforeSleeping;
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
        adjustPoorlyRestedEffect(player);
        this.applyTirednessReductionAfterSleep(player);
        clearLastUsedBed(player);
    }

    private static void adjustPoorlyRestedEffect(Player player) {
        int adjustment = getLastUsedBed(player).flatMap(TirednessLogic::getPoorlyRestedEffectLevel).orElse(-1);
        adjustPoorlyRestedEffect(player, adjustment);
    }

    private static Optional<Integer> getPoorlyRestedEffectLevel(BlockState bed) {
        if (bed.is(GenesisTags.Blocks.VERY_POOR_SLEEP_QUALITY)) {
            return Optional.of(VERY_POOR_SLEEP_QUALITY_EFFECT_LEVEL);
        }

        if (bed.is(GenesisTags.Blocks.POOR_SLEEP_QUALITY)) {
            return Optional.of(POOR_SLEEP_QUALITY_EFFECT_LEVEL);
        }

        return Optional.empty();
    }

    private static void adjustPoorlyRestedEffect(Player player, int amplifierAdjustment) {
        if (amplifierAdjustment == 0) {
            return;
        }

        MobEffectInstance effect = player.getEffect(GenesisMobEffects.POORLY_RESTED);

        int amplifier;

        if (effect == null) {
            amplifier = -1;
        } else {
            amplifier = effect.getAmplifier();
        }

        if (amplifier >= 0) {
            player.removeEffect(GenesisMobEffects.POORLY_RESTED);
        }

        int adjustedAmplifier = amplifier + amplifierAdjustment;

        if (adjustedAmplifier < 0) {
            return;
        }

        player.addEffect(new MobEffectInstance(
                GenesisMobEffects.POORLY_RESTED,
                MobEffectInstance.INFINITE_DURATION,
                adjustedAmplifier,
                false,
                false,
                false
        ));
    }

    private void applyTirednessReductionAfterSleep(Player player) {
        float reductionFactor = getLastUsedBed(player)
                .flatMap(TirednessLogic::getTirednessReductionFactor)
                .orElse(1F);

        int awakeTicks = this.calculateAwakeTicks(player, reductionFactor);
        this.setAwakeTicks(player, awakeTicks);
    }

    private static Optional<BlockState> getLastUsedBed(Player player) {
        return Optional.ofNullable(player.getAttached(GenesisAttachmentTypes.BED));
    }

    private static Optional<Float> getTirednessReductionFactor(BlockState bed) {
        if (getPoorlyRestedEffectLevel(bed).isPresent()) {
            MobEffect mobEffect = GenesisMobEffects.POORLY_RESTED.value();

            if (mobEffect instanceof PoorlyRestedMobEffect poorlyRestedMobEffect) {
                return Optional.of(poorlyRestedMobEffect.getReductionFactor());
            }

            return Optional.empty();
        }

        return bed.is(BlockTags.BEDS) ? Optional.of(1F) : Optional.empty();
    }

    private int calculateAwakeTicks(Player player, float reductionFactor) {
        int minimumTicksBeforeSleeping = this.minimumTicksBeforeSleeping;
        float awakeTickReduction = minimumTicksBeforeSleeping * reductionFactor;
        float adjustedAwakeTicks = getAwakeTicks(player) - awakeTickReduction;
        return (int) Mth.clamp(adjustedAwakeTicks, 0, minimumTicksBeforeSleeping);
    }

    public void setAwakeTicks(Player player, int awakeTicks) {
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

    private static void clearLastUsedBed(Player player) {
        player.removeAttached(GenesisAttachmentTypes.BED);
    }
}

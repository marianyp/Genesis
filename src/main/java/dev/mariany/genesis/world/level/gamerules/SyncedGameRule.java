package dev.mariany.genesis.world.level.gamerules;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import org.jetbrains.annotations.Nullable;

public class SyncedGameRule<T> {
    private final GameRule<T> gameRule;
    private final PayloadSupplier<T> payloadSupplier;

    private T value;

    public SyncedGameRule(GameRule<T> gameRule, T defaultValue, PayloadSupplier<T> payloadSupplier) {
        this.gameRule = gameRule;
        this.value = defaultValue;
        this.payloadSupplier = payloadSupplier;
    }

    public T getValue() {
        return this.value;
    }

    public void bootstrap() {
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerPlayConnectionEvents.JOIN.register(this::onPlayerJoin);
        GameRuleEvents.changeCallback(this.gameRule).register(this::onGameRuleChange);
    }

    private void onServerStarted(MinecraftServer server) {
        GameRules gameRules = server.getGameRules();
        T value = gameRules.get(this.gameRule);
        this.setValue(value, server);
    }

    private void onPlayerJoin(ServerGamePacketListenerImpl listener, PacketSender sender, MinecraftServer server) {
        this.syncValue(listener.player);
    }

    private void onGameRuleChange(T value, MinecraftServer server) {
        this.setValue(value, server);
    }

    public void setValue(T value) {
        this.setValue(value, null);
    }

    private void setValue(T value, @Nullable MinecraftServer server) {
        this.value = value;

        if (server == null) {
            return;
        }

        this.syncValue(server);
    }

    private void syncValue(MinecraftServer server) {
        PlayerLookup.all(server).forEach(this::syncValue);
    }

    private void syncValue(ServerPlayer player) {
        ServerPlayNetworking.send(player, this.payloadSupplier.get(this.getValue()));
    }

    @FunctionalInterface
    public interface PayloadSupplier<T> {
        CustomPacketPayload get(T value);
    }
}

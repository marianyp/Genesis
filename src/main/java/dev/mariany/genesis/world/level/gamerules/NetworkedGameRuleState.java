package dev.mariany.genesis.world.level.gamerules;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.gamerules.GameRule;

import java.util.function.Supplier;

public class NetworkedGameRuleState<T> extends GameRuleState<T> {
    private final PayloadSupplier<T> payloadSupplier;

    public NetworkedGameRuleState(Supplier<GameRule<T>> gameRuleSupplier, PayloadSupplier<T> payloadSupplier) {
        super(gameRuleSupplier);
        this.payloadSupplier = payloadSupplier;
    }

    @Override
    public void bootstrap() {
        super.bootstrap();
        ServerPlayConnectionEvents.JOIN.register(this::onPlayerJoin);
    }

    private void onPlayerJoin(ServerGamePacketListenerImpl listener, PacketSender sender, MinecraftServer server) {
        this.sendValue(listener.player);
    }

    @Override
    protected void setValue(T value, MinecraftServer server) {
        super.setValue(value, server);
        this.sendValue(server);
    }

    private void sendValue(MinecraftServer server) {
        PlayerLookup.all(server).forEach(this::sendValue);
    }

    private void sendValue(ServerPlayer player) {
        ServerPlayNetworking.send(player, this.payloadSupplier.get(this.getValue()));
    }

    @FunctionalInterface
    public interface PayloadSupplier<T> {
        CustomPacketPayload get(T value);
    }
}

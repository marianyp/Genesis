package dev.mariany.genesis.world.level.gamerules;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRules;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class GameRuleState<T> {
    private final Supplier<GameRule<T>> gameRuleSupplier;

    @Nullable
    private final ChangeCallback changeCallback;

    @Nullable
    private T value;

    public GameRuleState(Supplier<GameRule<T>> gameRuleSupplier) {
        this(gameRuleSupplier, null);
    }

    public GameRuleState(Supplier<GameRule<T>> gameRuleSupplier, @Nullable ChangeCallback changeCallback) {
        this.gameRuleSupplier = gameRuleSupplier;
        this.changeCallback = changeCallback;
    }

    public T getValue() {
        if (this.value == null) {
            throw new IllegalStateException("Attempted to retrieve game rule state before initialization.");
        }

        return this.value;
    }

    public void bootstrap() {
        GameRule<T> gameRule = this.gameRuleSupplier.get();

        this.value = gameRule.defaultValue();

        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        GameRuleEvents.changeCallback(gameRule).register(this::setValue);
    }

    private void onServerStarted(MinecraftServer server) {
        GameRules gameRules = server.getGameRules();
        T value = gameRules.get(this.gameRuleSupplier.get());
        this.setValue(value, server);
    }

    public void setValue(T value) {
        this.value = value;
    }

    protected void setValue(T value, MinecraftServer server) {
        this.value = value;

        if (this.changeCallback != null) {
            this.changeCallback.onChange(server);
        }
    }

    @FunctionalInterface
    public interface ChangeCallback {
        void onChange(MinecraftServer server);
    }
}

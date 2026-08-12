package dev.mariany.genesis.loot;

import dev.mariany.genesis.block.custom.cauldron.FilledPrimitiveCauldronBlock;
import dev.mariany.genesis.loot.collect.LootTableIngredientCollector;
import dev.mariany.genesis.packet.clientbound.UpdatePrimitiveCauldronLootPayload;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public class PrimitiveCauldronLoot {
    private final Map<ResourceKey<LootTable>, List<Ingredient>> lootTableToIngredients = new HashMap<>();
    private final CopyOnWriteArrayList<Runnable> updateListeners = new CopyOnWriteArrayList<>();

    public void bootstrap() {
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(this::onDataPackReload);
        ServerPlayConnectionEvents.JOIN.register(this::onPlayerJoin);
    }

    private void onServerStarted(MinecraftServer server) {
        sync(server);
    }

    private void onDataPackReload(
            MinecraftServer server,
            CloseableResourceManager resourceManager,
            boolean success
    ) {
        if (!success) {
            return;
        }

        sync(server);
    }

    private void sync(MinecraftServer server) {
        this.update(server);
        UpdatePrimitiveCauldronLootPayload payload = this.createPayload();
        PlayerLookup.all(server).forEach(player -> ServerPlayNetworking.send(player, payload));
    }

    private void update(MinecraftServer server) {
        Map<ResourceKey<LootTable>, List<Ingredient>> possibleDrops = new HashMap<>();
        LootTableIngredientCollector collector = new LootTableIngredientCollector(server);

        streamFilledPrimitiveCauldronBlocks()
                .map(FilledPrimitiveCauldronBlock::getPrimitiveLootTable)
                .distinct()
                .forEach(lootTable -> possibleDrops.put(
                        lootTable,
                        collector.collect(lootTable)
                ));

        this.update(possibleDrops);
    }

    public void update(Map<ResourceKey<LootTable>, List<Ingredient>> possibleDrops) {
        Map<ResourceKey<LootTable>, List<Ingredient>> copy = new HashMap<>();

        possibleDrops.forEach((lootTable, ingredients) -> copy.put(
                lootTable,
                List.copyOf(ingredients)
        ));

        this.lootTableToIngredients.clear();
        this.lootTableToIngredients.putAll(copy);

        this.updateListeners.forEach(Runnable::run);
    }

    public void addUpdateListener(Runnable listener) {
        this.updateListeners.addIfAbsent(listener);
    }

    private void onPlayerJoin(
            ServerGamePacketListenerImpl listener,
            PacketSender packetSender,
            MinecraftServer server
    ) {
        ServerPlayNetworking.send(listener.player, createPayload());
    }

    private UpdatePrimitiveCauldronLootPayload createPayload() {
        return new UpdatePrimitiveCauldronLootPayload(this.getPossibleDrops());
    }

    public List<Ingredient> getPossibleDrops(ResourceKey<LootTable> lootTable) {
        return this.lootTableToIngredients.getOrDefault(lootTable, List.of());
    }

    public Map<ResourceKey<LootTable>, List<Ingredient>> getPossibleDrops() {
        return this.lootTableToIngredients;
    }

    public static Stream<FilledPrimitiveCauldronBlock> streamFilledPrimitiveCauldronBlocks() {
        return BuiltInRegistries.BLOCK
                .stream()
                .flatMap(block -> block instanceof FilledPrimitiveCauldronBlock filledCauldron
                        ? Stream.of(filledCauldron)
                        : Stream.empty());
    }
}

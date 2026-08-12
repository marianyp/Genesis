package dev.mariany.genesis.loot.collect;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dev.mariany.genesis.Genesis;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class LootTableIngredientCollector {
    private final MinecraftServer server;
    private final RegistryOps<JsonElement> ops;

    public LootTableIngredientCollector(MinecraftServer server) {
        this.server = server;

        this.ops = server
                .reloadableRegistries()
                .lookup()
                .createSerializationContext(JsonOps.INSTANCE);
    }

    public List<Ingredient> collect(ResourceKey<LootTable> lootTableKey) {
        return List.copyOf(collect(lootTableKey, new LinkedHashSet<>()));
    }

    Set<Ingredient> collect(ResourceKey<LootTable> lootTableKey, Set<ResourceKey<LootTable>> visitedLootTables) {
        Set<Ingredient> ingredients = new LinkedHashSet<>();

        if (!visitedLootTables.add(lootTableKey)) {
            return ingredients;
        }

        LootTable lootTable = this.server.reloadableRegistries().getLootTable(lootTableKey);

        LootTable.DIRECT_CODEC
                .encodeStart(this.ops, lootTable)
                .resultOrPartial(error -> Genesis.LOGGER.warn(
                        "Unable to inspect loot table {}: {}",
                        lootTableKey.identifier(),
                        error
                ))
                .ifPresent(json -> inspect(json, ingredients, visitedLootTables));

        return ingredients;
    }

    void inspect(JsonElement element, Set<Ingredient> ingredients, Set<ResourceKey<LootTable>> visitedLootTables) {
        if (element.isJsonArray()) {
            element.getAsJsonArray().forEach(child -> inspect(child, ingredients, visitedLootTables));
            return;
        }

        if (!element.isJsonObject()) {
            return;
        }

        JsonObject object = element.getAsJsonObject();

        inspectHandler(getString(object, "type"), object, ingredients, visitedLootTables);
        inspectHandler(getString(object, "function"), object, ingredients, visitedLootTables);

        object.entrySet()
              .forEach(entry -> inspect(entry.getValue(), ingredients, visitedLootTables));
    }

    private void inspectHandler(
            String type,
            JsonObject object,
            Set<Ingredient> ingredients,
            Set<ResourceKey<LootTable>> visitedLootTables
    ) {
        LootTableIngredientHandlers
                .get(type)
                .ifPresent(handler -> handler.collect(this, object, ingredients, visitedLootTables));
    }

    private static String getString(JsonObject object, String field) {
        JsonElement value = object.get(field);
        return value != null && value.isJsonPrimitive() ? value.getAsString() : "";
    }
}

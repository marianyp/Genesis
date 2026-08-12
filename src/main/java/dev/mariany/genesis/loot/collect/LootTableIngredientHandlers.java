package dev.mariany.genesis.loot.collect;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class LootTableIngredientHandlers {
    private static final Map<Identifier, Handler> ALL = new LinkedHashMap<>();

    static {
        register(
                "item",
                (
                        collector,
                        object,
                        ingredients,
                        visited
                ) -> addItem(object, "name", ingredients)
        );

        register(
                "loot_table",
                (
                        collector,
                        object,
                        ingredients,
                        visited
                ) -> collectNestedLootTable(
                        collector,
                        object.get("value"),
                        ingredients,
                        visited
                )
        );

        register(
                "set_item",
                (
                        collector,
                        object,
                        ingredients,
                        visited
                ) -> addItem(object, "item", ingredients)
        );

        register(
                "tag",
                (
                        collector,
                        object,
                        ingredients,
                        visited
                ) -> addTag(object, ingredients)
        );
    }

    private LootTableIngredientHandlers() {
    }

    public static Optional<Handler> get(String value) {
        return Optional.ofNullable(Identifier.tryParse(value)).map(ALL::get);
    }

    private static void register(String path, Handler handler) {
        Identifier id = Identifier.withDefaultNamespace(path);
        Handler previous = ALL.putIfAbsent(id, handler);

        if (previous != null) {
            throw new IllegalStateException("Duplicate loot table ingredient handler: " + id);
        }
    }

    private static void addTag(JsonObject object, Set<Ingredient> ingredients) {
        Identifier tagId = Identifier.tryParse(getString(object, "name"));

        if (tagId == null) {
            return;
        }

        TagKey<Item> tag = TagKey.create(Registries.ITEM, tagId);
        BuiltInRegistries.ITEM.get(tag).map(Ingredient::of).ifPresent(ingredients::add);
    }

    private static void addItem(JsonObject object, String field, Set<Ingredient> ingredients) {
        Identifier itemId = Identifier.tryParse(getString(object, field));

        if (itemId == null) {
            return;
        }

        BuiltInRegistries.ITEM
                .get(itemId)
                .map(Holder.Reference::value)
                .map(Ingredient::of)
                .ifPresent(ingredients::add);
    }

    private static String getString(JsonObject object, String field) {
        JsonElement value = object.get(field);
        return value != null && value.isJsonPrimitive() ? value.getAsString() : "";
    }

    private static void collectNestedLootTable(
            LootTableIngredientCollector collector,
            JsonElement value,
            Set<Ingredient> ingredients,
            Set<ResourceKey<LootTable>> visitedLootTables
    ) {
        if (value == null) {
            return;
        }

        if (value.isJsonObject()) {
            collector.inspect(value, ingredients, visitedLootTables);
            return;
        }

        Identifier lootTableId = Identifier.tryParse(value.getAsString());

        if (lootTableId == null) {
            return;
        }

        ResourceKey<LootTable> lootTableKey = ResourceKey.create(Registries.LOOT_TABLE, lootTableId);
        ingredients.addAll(collector.collect(lootTableKey, visitedLootTables));
    }

    @FunctionalInterface
    public interface Handler {
        void collect(
                LootTableIngredientCollector collector,
                JsonObject object,
                Set<Ingredient> ingredients,
                Set<ResourceKey<LootTable>> visitedLootTables
        );
    }
}

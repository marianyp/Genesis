package dev.mariany.genesis.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import dev.mariany.genesis.Genesis;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

public class GenesisLootItemFunctions {
    static {
        register("biome_map", BiomeMapFunction.MAP_CODEC);
    }

    private static void register(String id, MapCodec<? extends LootItemFunction> codec) {
        Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, Genesis.id(id), codec);
    }

    public static void bootstrap() {
        Genesis.bootstrapLog("Loot Item Functions");
    }
}

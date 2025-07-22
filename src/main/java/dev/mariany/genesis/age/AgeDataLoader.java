package dev.mariany.genesis.age;

import com.google.common.collect.ImmutableMap;
import dev.mariany.genesis.registry.GenesisRegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.Map;

public class AgeDataLoader extends JsonDataLoader<Age> {
    public AgeDataLoader(RegistryWrapper.WrapperLookup registries) {
        super(registries, Age.CODEC, GenesisRegistryKeys.AGE);
    }

    @Override
    protected void apply(Map<Identifier, Age> map, ResourceManager manager, Profiler profiler) {
        AgeManager ageManager = AgeManager.getInstance();
        ageManager.clear();

        ImmutableMap.Builder<Identifier, AgeEntry> builder = ImmutableMap.builder();

        map.forEach((id, age) -> builder.put(id, new AgeEntry(id, age)));

        builder.buildOrThrow().values().forEach(ageManager::add);
    }
}

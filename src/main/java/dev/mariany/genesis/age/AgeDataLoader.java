package dev.mariany.genesis.age;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.registry.GenesisRegistryKeys;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AgeDataLoader implements SimpleSynchronousResourceReloadListener {
    public static final Identifier ID = Genesis.id("ages");

    private final RegistryWrapper.WrapperLookup wrapperLookup;

    public AgeDataLoader(RegistryWrapper.WrapperLookup wrapperLookup) {
        this.wrapperLookup = wrapperLookup;
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public void reload(ResourceManager manager) {
        AgeManager ageManager = AgeManager.getInstance();
        ageManager.clear();

        List<AgeEntry> loadedAges = new ArrayList<>();

        RegistryOps<JsonElement> ops = this.wrapperLookup.getOps(JsonOps.INSTANCE);

        String resourceId = GenesisRegistryKeys.AGE.getValue().getPath();

        for (Map.Entry<Identifier, Resource> entry : manager.findResources(resourceId, path -> path.toString().endsWith(".json")).entrySet()) {
            Resource resource = entry.getValue();
            Identifier key = entry.getKey();
            String path = key.getPath();
            String strippedPath = StringUtils.removeEnd(path, ".json");
            Identifier id = Identifier.of(
                    key.getNamespace(),
                    StringUtils.removeStart(strippedPath, resourceId + "/")
            );

            try (InputStreamReader reader = new InputStreamReader(resource.getInputStream())) {
                JsonElement json = JsonParser.parseReader(reader);

                DataResult<Age> result = Age.CODEC.parse(ops, json);

                result.resultOrPartial(error -> Genesis.LOGGER.error("Failed to parse {}: {}", id, error))
                        .ifPresent(age -> loadedAges.add(new AgeEntry(id, age)));

            } catch (IOException error) {
                Genesis.LOGGER.error("Error occurred while loading resource json: {}", id, error);
            }
        }

        ageManager.addAll(loadedAges);
    }
}

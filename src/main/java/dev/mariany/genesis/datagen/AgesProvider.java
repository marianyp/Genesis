package dev.mariany.genesis.datagen;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dev.mariany.genesis.age.Age;
import dev.mariany.genesis.age.AgeEntry;
import dev.mariany.genesis.registry.GenesisRegistryKeys;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class AgesProvider implements DataProvider {
    protected final FabricDataOutput output;
    private final DataOutput.PathResolver pathResolver;
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup;

    public AgesProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        this.output = output;
        this.pathResolver = output.getResolver(GenesisRegistryKeys.AGE);
        this.registryLookup = registryLookup;
    }

    public abstract void generateAges(RegistryWrapper.WrapperLookup registryLookup, Consumer<AgeEntry> consumer);

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return this.registryLookup.thenCompose(lookup -> {
            final Set<Identifier> identifiers = Sets.newHashSet();
            final Set<AgeEntry> ages = Sets.newHashSet();

            generateAges(lookup, ages::add);

            RegistryOps<JsonElement> ops = lookup.getOps(JsonOps.INSTANCE);
            final List<CompletableFuture<?>> futures = new ArrayList<>();

            for (AgeEntry ageEntry : ages) {
                Identifier id = ageEntry.getId();

                if (!identifiers.add(id)) {
                    throw new IllegalStateException("Duplicate age " + ageEntry.getId());
                }

                JsonObject advancementJson = Age.CODEC.encodeStart(ops, ageEntry.getAge())
                        .getOrThrow(IllegalStateException::new).getAsJsonObject();

                FabricDataGenHelper.addConditions(advancementJson, FabricDataGenHelper.consumeConditions(ageEntry));

                futures.add(DataProvider.writeToPath(writer, advancementJson, getOutputPath(ageEntry)));
            }

            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        });
    }

    private Path getOutputPath(AgeEntry age) {
        return pathResolver.resolveJson(age.getId());
    }
}

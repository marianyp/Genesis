package dev.mariany.genesis.datagen;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dev.mariany.genesis.instruction.Instruction;
import dev.mariany.genesis.instruction.InstructionEntry;
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

public abstract class InstructionsProvider implements DataProvider {
    protected final FabricDataOutput output;
    private final DataOutput.PathResolver pathResolver;
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup;

    public InstructionsProvider(
            FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup
    ) {
        this.output = output;
        this.pathResolver = output.getResolver(GenesisRegistryKeys.INSTRUCTION);
        this.registryLookup = registryLookup;
    }

    public abstract void generateInstructions(
            RegistryWrapper.WrapperLookup registryLookup,
            Consumer<InstructionEntry> consumer
    );

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return this.registryLookup.thenCompose(lookup -> {
            final Set<Identifier> identifiers = Sets.newHashSet();
            final Set<InstructionEntry> instructions = Sets.newHashSet();

            generateInstructions(lookup, instructions::add);

            RegistryOps<JsonElement> ops = lookup.getOps(JsonOps.INSTANCE);
            final List<CompletableFuture<?>> futures = new ArrayList<>();

            for (InstructionEntry instructionEntry : instructions) {
                Identifier id = instructionEntry.getId();

                if (!identifiers.add(id)) {
                    throw new IllegalStateException("Duplicate instruction " + instructionEntry.getId());
                }

                JsonObject advancementJson = Instruction.CODEC.encodeStart(
                        ops,
                        instructionEntry.getInstruction()
                ).getOrThrow(IllegalStateException::new).getAsJsonObject();

                FabricDataGenHelper.addConditions(
                        advancementJson,
                        FabricDataGenHelper.consumeConditions(instructionEntry)
                );

                futures.add(DataProvider.writeToPath(writer, advancementJson, getOutputPath(instructionEntry)));
            }

            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        });
    }

    private Path getOutputPath(InstructionEntry instruction) {
        return pathResolver.resolveJson(instruction.getId());
    }
}

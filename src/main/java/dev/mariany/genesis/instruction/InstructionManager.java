package dev.mariany.genesis.instruction;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InstructionManager {
    private static final InstructionManager INSTANCE = new InstructionManager();

    private final Map<Identifier, InstructionEntry> instructions = new Object2ObjectOpenHashMap<>();

    public static InstructionManager getInstance() {
        return INSTANCE;
    }

    public Optional<InstructionEntry> find(AdvancementEntry advancementEntry) {
        return instructions
                .values()
                .stream()
                .filter(
                        instructionEntry ->
                                instructionEntry.getAdvancementEntry().id().equals(advancementEntry.id())
                )
                .findAny();
    }

    public Collection<InstructionEntry> getInstructions() {
        return this.instructions.values();
    }

    public List<Identifier> getInstructionAdvancementIds() {
        return this.instructions.values()
                .stream()
                .map(instructionEntry -> instructionEntry.getAdvancementEntry().id()).toList();
    }

    protected void add(InstructionEntry instructionEntry) {
        this.instructions.put(instructionEntry.getId(), instructionEntry);
    }

    protected void clear() {
        this.instructions.clear();
    }
}

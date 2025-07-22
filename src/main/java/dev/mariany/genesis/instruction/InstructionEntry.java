package dev.mariany.genesis.instruction;

import dev.mariany.genesis.Genesis;
import net.minecraft.advancement.*;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Optional;

public class InstructionEntry {
    public static final String ADVANCEMENT_PREFIX = "instruction/";
    public static final Identifier ROOT_ADVANCEMENT_ID = Genesis.id(ADVANCEMENT_PREFIX + "root");

    private final Identifier id;
    private final Instruction instruction;
    private final AdvancementEntry advancementEntry;

    public InstructionEntry(Identifier id, Instruction instruction) {
        this.id = id;
        this.instruction = instruction;
        this.advancementEntry = createAdvancementEntry(id, instruction);
    }

    private AdvancementEntry createAdvancementEntry(Identifier id, Instruction instruction) {
        return new AdvancementEntry(getAdvancementId(id), createAdvancement(instruction));
    }

    public static Advancement createAdvancement(Instruction instruction) {
        Identifier parent = instruction.parent()
                .map(InstructionEntry::getAdvancementId)
                .orElse(ROOT_ADVANCEMENT_ID);

        Map<String, AdvancementCriterion<?>> criteria = instruction.criteria();

        AdvancementRequirements requirements = instruction.requirements().isEmpty() ?
                AdvancementRequirements.allOf(criteria.keySet()) :
                instruction.requirements();

        return new Advancement(
                Optional.of(parent),
                Optional.of(createAdvancementDisplay(instruction)),
                AdvancementRewards.NONE,
                criteria,
                requirements,
                false
        );
    }

    private static AdvancementDisplay createAdvancementDisplay(Instruction instruction) {
        InstructionDisplay instructionDisplay = instruction.display();

        return new AdvancementDisplay(
                instructionDisplay.icon(),
                instructionDisplay.title(),
                instructionDisplay.description(),
                Optional.empty(),
                AdvancementFrame.TASK,
                false,
                false,
                false
        );
    }

    public static Identifier getAdvancementId(Identifier id) {
        return id.withPrefixedPath(ADVANCEMENT_PREFIX);
    }

    public Identifier getId() {
        return this.id;
    }

    public Instruction getInstruction() {
        return this.instruction;
    }

    public AdvancementEntry getAdvancementEntry() {
        return this.advancementEntry;
    }
}

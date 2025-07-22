package dev.mariany.genesis.instruction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public record Instruction(
        Optional<Identifier> parent,
        Map<String, AdvancementCriterion<?>> criteria,
        AdvancementRequirements requirements,
        InstructionDisplay display
) {
    private static final Codec<Map<String, AdvancementCriterion<?>>> CRITERIA_CODEC = Codec.unboundedMap(
            Codec.STRING,
            AdvancementCriterion.CODEC
    );
    public static final Codec<Instruction> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Identifier.CODEC.optionalFieldOf("parent").forGetter(Instruction::parent),
                            CRITERIA_CODEC.optionalFieldOf("criteria", new HashMap<>()).forGetter(Instruction::criteria),
                            AdvancementRequirements.CODEC.optionalFieldOf("requirements", AdvancementRequirements.EMPTY)
                                    .forGetter(Instruction::requirements),
                            InstructionDisplay.CODEC.fieldOf("display").forGetter(Instruction::display)
                    )
                    .apply(instance, Instruction::new)
    );

    public static class Builder {
        @Nullable
        private Identifier parent = null;
        private final Map<String, AdvancementCriterion<?>> criteria = new HashMap<>();
        private AdvancementRequirements requirements = AdvancementRequirements.EMPTY;
        private InstructionDisplay display;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder parent(Identifier parent) {
            this.parent = parent;
            return this;
        }

        public Builder requirements(AdvancementRequirements advancementRequirements) {
            this.requirements = advancementRequirements;
            return this;
        }

        public Builder criterion(String name, AdvancementCriterion<?> criterion) {
            this.criteria.put(name, criterion);
            return this;
        }

        public Builder display(InstructionDisplay display) {
            this.display = display;
            return this;
        }

        public Builder display(Item icon, Text title) {
            return display(icon, title, Text.empty());
        }

        public Builder display(Item icon, Text title, Text description) {
            this.display = new InstructionDisplay(icon.getDefaultStack(), title, description);
            return this;
        }

        public InstructionEntry build(Identifier id) {
            if (this.requirements.isEmpty()) {
                this.requirements = AdvancementRequirements.allOf(this.criteria.keySet());
            }

            return new InstructionEntry(id, new Instruction(
                    Optional.ofNullable(this.parent),
                    this.criteria,
                    this.requirements,
                    this.display
            ));
        }

        public void build(Consumer<InstructionEntry> exporter, Identifier id) {
            InstructionEntry instructionEntry = this.build(id);
            exporter.accept(instructionEntry);
        }
    }
}

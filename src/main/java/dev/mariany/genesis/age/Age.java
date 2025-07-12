package dev.mariany.genesis.age;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mariany.genesis.advancement.criterion.ObtainAdvancementCriterion;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public record Age(
        List<Ingredient> unlocks,
        Optional<Identifier> parent,
        boolean requiresParent,
        Map<String, AdvancementCriterion<?>> criteria,
        AgeDisplay display
) {
    private static final Codec<Map<String, AdvancementCriterion<?>>> CRITERIA_CODEC = Codec.unboundedMap(Codec.STRING, AdvancementCriterion.CODEC);
    public static final Codec<Age> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Ingredient.CODEC.listOf().fieldOf("unlocks").forGetter(Age::unlocks),
                            Identifier.CODEC.optionalFieldOf("parent").forGetter(Age::parent),
                            Codec.BOOL.optionalFieldOf("requires_parent")
                                    .xmap(opt -> opt.orElse(true), Optional::of)
                                    .forGetter(Age::requiresParent),
                            CRITERIA_CODEC.fieldOf("criteria").forGetter(Age::criteria),
                            AgeDisplay.CODEC.fieldOf("display").forGetter(Age::display)
                    )
                    .apply(instance, Age::new)
    );

    public static class Builder {
        private final List<Ingredient> unlocks = new ArrayList<>();
        @Nullable
        private Identifier parent = null;
        private boolean requiresParent = true;
        private final Map<String, AdvancementCriterion<?>> criteria = new HashMap<>();
        private AgeDisplay display;

        private Builder() {
        }

        public static Age.Builder create() {
            return new Age.Builder();
        }

        public Builder addUnlock(Ingredient ingredient) {
            this.unlocks.add(ingredient);
            return this;
        }

        public Builder unlocks(List<Ingredient> ingredients) {
            this.unlocks.clear();
            this.unlocks.addAll(ingredients);
            return this;
        }

        public Builder parent(Identifier parent) {
            this.parent = parent;
            return this;
        }

        public Builder parentOptional() {
            this.requiresParent = false;
            return this;
        }

        public Builder requireAge(Identifier id) {
            Optional<String> categoryOpt = AgeEntry.getCategory(id);
            Optional<String> subpathOpt = AgeEntry.getSubPath(id);

            String name = subpathOpt.map(
                    subpath -> categoryOpt
                            .map(category -> "has_" + subpath + "_" + category + "_age")
                            .orElse("has_" + subpath + "_age")
            ).orElse("has_age");

            return criterion(name, ObtainAdvancementCriterion.Conditions.create(AgeEntry.getAdvancementId(id)));
        }

        public Builder criterion(String name, AdvancementCriterion<?> criterion) {
            this.criteria.put(name, criterion);
            return this;
        }

        public Builder display(AgeDisplay display) {
            this.display = display;
            return this;
        }

        public Builder display(Item icon, Text title, Text description) {
            this.display = new AgeDisplay(icon.getDefaultStack(), title, description);
            return this;
        }

        public AgeEntry build(Identifier id) {
            Map<String, AdvancementCriterion<?>> map = new HashMap<>(this.criteria);
            return new AgeEntry(id, new Age(
                    this.unlocks,
                    Optional.ofNullable(this.parent),
                    this.requiresParent,
                    map,
                    this.display
            ));
        }

        public void build(Consumer<AgeEntry> exporter, Identifier id) {
            AgeEntry ageEntry = this.build(id);
            exporter.accept(ageEntry);
        }
    }
}

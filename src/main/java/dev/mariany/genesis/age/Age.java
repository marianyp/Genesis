package dev.mariany.genesis.age;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mariany.genesis.advancement.criterion.ObtainAdvancementCriterion;
import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public record Age(
        Category category,
        List<Ingredient> unlocks,
        Optional<Identifier> parent,
        Map<String, AdvancementCriterion<?>> criteria,
        AgeDisplay display
) {
    private static final Codec<Map<String, AdvancementCriterion<?>>> CRITERIA_CODEC = Codec.unboundedMap(Codec.STRING, AdvancementCriterion.CODEC);
    public static final Codec<Age> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Category.CODEC.fieldOf("category").forGetter(Age::category),
                            Ingredient.CODEC.listOf().fieldOf("unlocks").forGetter(Age::unlocks),
                            Identifier.CODEC.optionalFieldOf("parent").forGetter(Age::parent),
                            CRITERIA_CODEC.fieldOf("criteria").forGetter(Age::criteria),
                            AgeDisplay.CODEC.fieldOf("display").forGetter(Age::display)
                    )
                    .apply(instance, Age::new)
    );

    public Advancement createAdvancement() {
        Identifier parent = this.parent
                .map(AgeEntry::getAdvancementId)
                .orElse(AgeEntry.ROOT_ADVANCEMENT_ID);

        boolean alert = true;

        Map<String, AdvancementCriterion<?>> advancementCriteria = new HashMap<>(this.criteria);

        if (advancementCriteria.isEmpty()) {
            advancementCriteria.put("root", new AdvancementCriterion<>(Criteria.TICK, TickCriterion.Conditions.createTick().conditions()));
            alert = false;
        }

        return new Advancement(
                Optional.of(parent),
                Optional.of(createAdvancementDisplay(this.display, alert)),
                AdvancementRewards.NONE,
                advancementCriteria,
                AdvancementRequirements.allOf(advancementCriteria.keySet()),
                false
        );
    }

    private AdvancementDisplay createAdvancementDisplay(AgeDisplay ageDisplay, boolean alert) {
        Text title = Text.translatable("age.genesis.title",
                Text.translatable("age.genesis.category." + this.category.name),
                ageDisplay.title(),
                Text.translatable("age.genesis.age")
        );

        return new AdvancementDisplay(
                ageDisplay.icon(),
                title,
                ageDisplay.description(),
                Optional.empty(),
                AdvancementFrame.GOAL,
                alert,
                alert,
                false
        );
    }

    public static class Builder {
        private final Category category;
        private final List<Ingredient> unlocks = new ArrayList<>();
        @Nullable
        private Identifier parent = null;
        private final Map<String, AdvancementCriterion<?>> criteria = new HashMap<>();
        private AgeDisplay display;

        private Builder(Category category) {
            this.category = category;
        }

        public static Age.Builder create(Category category) {
            return new Age.Builder(category);
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
            this.parent = getNestedId(parent);
            return this;
        }

        public Builder noParent() {
            this.parent = null;
            return this;
        }

        public Builder requireAge(Age.Category category, Identifier id) {
            String name = "has_" + id.getPath() + "_" + category.name + "_age";
            return criterion(name, ObtainAdvancementCriterion.Conditions.create(AgeEntry.getAdvancementId(category, id)));
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
            return new AgeEntry(id, new Age(this.category, this.unlocks, Optional.ofNullable(this.parent), map, this.display));
        }

        public void build(Consumer<AgeEntry> exporter, Identifier id) {
            AgeEntry ageEntry = this.build(getNestedId(id));
            exporter.accept(ageEntry);
        }

        private Identifier getNestedId(Identifier id) {
            return id.withPrefixedPath(category.asString() + "/");
        }
    }

    public enum Category implements StringIdentifiable {
        ARMOR("armor"),
        BLOCKS("blocks"),
        TOOLS("tools");

        public static final Codec<Category> CODEC = StringIdentifiable.createCodec(Category::values);

        private final String name;

        Category(final String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }
}

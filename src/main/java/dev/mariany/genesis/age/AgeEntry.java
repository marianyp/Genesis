package dev.mariany.genesis.age;

import dev.mariany.genesis.Genesis;
import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AgeEntry {
    public static final String ADVANCEMENT_PREFIX = "age/";
    public static final Identifier ROOT_ADVANCEMENT_ID = Genesis.id(ADVANCEMENT_PREFIX + "root");

    private final Identifier id;
    private final Age age;
    private final AdvancementEntry advancementEntry;

    public AgeEntry(Identifier id, Age age) {
        this.id = id;
        this.age = age;
        this.advancementEntry = createAdvancementEntry(id, age);
    }

    private AdvancementEntry createAdvancementEntry(Identifier id, Age age) {
        return new AdvancementEntry(getAdvancementId(this), createAdvancement(id, age));
    }

    public static Advancement createAdvancement(Identifier id, Age age) {
        Identifier parent = age.parent()
                .map(AgeEntry::getAdvancementId)
                .orElse(AgeEntry.ROOT_ADVANCEMENT_ID);

        boolean alert = true;

        Map<String, AdvancementCriterion<?>> advancementCriteria = new HashMap<>(age.criteria());

        if (advancementCriteria.isEmpty()) {
            advancementCriteria.put("root", new AdvancementCriterion<>(Criteria.TICK, TickCriterion.Conditions.createTick().conditions()));
            alert = false;
        }

        return new Advancement(
                Optional.of(parent),
                Optional.of(createAdvancementDisplay(id, age.display(), alert)),
                AdvancementRewards.NONE,
                advancementCriteria,
                AdvancementRequirements.allOf(advancementCriteria.keySet()),
                false
        );
    }

    private static AdvancementDisplay createAdvancementDisplay(Identifier id, AgeDisplay ageDisplay, boolean alert) {
        MutableText title = getCategory(id)
                .map(category -> Text.translatable("age.genesis.category." + category)
                        .append(Text.literal(" "))
                        .append(Text.translatable("age.genesis.age")))
                .orElseGet(() -> Text.translatable("age.genesis.title",
                        ageDisplay.title(),
                        Text.translatable("age.genesis.age")));

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

    public static Optional<String> getCategory(Identifier id) {
        String path = id.getPath();
        int index = path.indexOf('/');

        if (index >= 0) {
            return Optional.of(path.substring(0, index));
        }

        return Optional.empty();
    }

    public static Optional<String> getSubPath(Identifier id) {
        String path = id.getPath();
        int index = path.indexOf('/');

        if (index >= 0 && index + 1 < path.length()) {
            return Optional.of(path.substring(index + 1));
        }

        return Optional.empty();
    }


    public static Identifier getAdvancementId(AgeEntry ageEntry) {
        return getAdvancementId(ageEntry.getId());
    }

    public static Identifier getAdvancementId(Identifier id) {
        return id.withPrefixedPath(ADVANCEMENT_PREFIX);
    }

    public Identifier getId() {
        return this.id;
    }

    public Age getAge() {
        return this.age;
    }

    public AdvancementEntry getAdvancementEntry() {
        return this.advancementEntry;
    }

    public boolean isDone(ServerPlayerEntity player) {
        return player.getAdvancementTracker().getProgress(this.advancementEntry).isDone();
    }

    public Optional<Identifier> getParentAdvancementId() {
        return this.advancementEntry.value().parent();
    }
}

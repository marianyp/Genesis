package dev.mariany.genesis.age;

import dev.mariany.genesis.Genesis;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

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
        this.advancementEntry = this.createAdvancementEntry(age);
    }

    private AdvancementEntry createAdvancementEntry(Age age) {
        return new AdvancementEntry(getAdvancementId(this), age.createAdvancement());
    }

    public static Identifier getAdvancementId(AgeEntry ageEntry) {
        return getAdvancementId(ageEntry.getId());
    }

    public static Identifier getAdvancementId(Identifier id) {
        return id.withPrefixedPath(ADVANCEMENT_PREFIX);
    }

    public static Identifier getAdvancementId(Age.Category category, Identifier id) {
        return id.withPrefixedPath(ADVANCEMENT_PREFIX + category.asString() + "/");
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

    public Optional<Identifier> getParent() {
        return this.advancementEntry.value().parent();
    }
}

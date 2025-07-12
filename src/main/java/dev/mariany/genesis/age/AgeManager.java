package dev.mariany.genesis.age;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.*;

public class AgeManager {
    private static final AgeManager INSTANCE = new AgeManager();

    private final Map<Identifier, AgeEntry> ages = new Object2ObjectOpenHashMap<>();

    public static AgeManager getInstance() {
        return INSTANCE;
    }

    public boolean isUnlocked(ServerPlayerEntity player, Block block) {
        return isUnlocked(player, block.asItem().getDefaultStack());
    }

    public boolean isUnlocked(ServerPlayerEntity player, ItemStack stack) {
        if (player.isCreative()) {
            return true;
        }

        List<AgeEntry> requiredAges = getRequiredAges(stack);
        return requiredAges.isEmpty() || requiredAges.stream().allMatch(placedAge -> placedAge.isDone(player));
    }

    public List<AgeEntry> getRequiredAges(ItemStack stack) {
        List<AgeEntry> requiredAges = new ArrayList<>();

        for (AgeEntry placedAge : this.ages.values()) {
            List<Ingredient> unlocks = placedAge.getAge().unlocks();
            for (Ingredient ingredient : unlocks) {
                if (ingredient.test(stack)) {
                    requiredAges.add(placedAge);
                    break;
                }
            }
        }

        return requiredAges;
    }

    public Optional<AgeEntry> find(AdvancementEntry advancementEntry) {
        return ages.values().stream().filter(
                ageEntry -> ageEntry.getAdvancementEntry().id().equals(advancementEntry.id())
        ).findAny();
    }

    public Optional<AgeEntry> get(Identifier id) {
        return Optional.ofNullable(this.ages.get(id));
    }

    public Collection<AgeEntry> getAges() {
        return this.ages.values();
    }

    public List<Ingredient> getAllUnlocks(ServerPlayerEntity player) {
        return getAges()
                .stream()
                .filter(ageEntry -> !ageEntry.isDone(player))
                .flatMap(ageEntry -> ageEntry.getAge().unlocks().stream())
                .toList();
    }

    protected void add(AgeEntry age) {
        this.ages.put(age.getId(), age);
    }

    protected void clear() {
        this.ages.clear();
    }
}

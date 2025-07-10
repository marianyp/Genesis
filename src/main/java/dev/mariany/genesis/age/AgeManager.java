package dev.mariany.genesis.age;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.util.*;

public class AgeManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final AgeManager INSTANCE = new AgeManager();

    private final Map<Identifier, AgeEntry> ages = new Object2ObjectOpenHashMap<>();
    private final Map<Age.Category, Map<Identifier, AgeEntry>> categories = new Object2ObjectOpenHashMap<>();

    public static AgeManager getInstance() {
        return INSTANCE;
    }

    public boolean isUnlocked(ServerPlayerEntity player, ItemStack stack) {
        List<AgeEntry> requiredAges = getRequiredAges(stack);
        return requiredAges.isEmpty() || requiredAges.stream().allMatch(placedAge -> placedAge.isDone(player));
    }

    public boolean isUnlocked(ServerPlayerEntity player, BlockState state) {
        return isUnlocked(player, state.getBlock().asItem().getDefaultStack());
    }

    public boolean isUnlocked(ServerPlayerEntity player, Block block) {
        return isUnlocked(player, block.asItem().getDefaultStack());
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

    public Collection<AgeEntry> getAges() {
        return this.ages.values();
    }

    public Collection<AgeEntry> getAges(Age.Category category) {
        return this.categories.getOrDefault(category, new Object2ObjectOpenHashMap<>()).values();
    }

    private boolean tryAdd(AgeEntry age) {
        Identifier id = age.getId();

        Age.Category category = age.getAge().category();
        Map<Identifier, AgeEntry> categoryAges = this.categories.getOrDefault(category, new Object2ObjectOpenHashMap<>());

        categoryAges.put(id, age);
        this.categories.put(category, categoryAges);
        this.ages.put(id, age);

        return true;
    }

    private void remove(AgeEntry ageEntry) {
        Identifier id = ageEntry.getId();
        Age.Category category = ageEntry.getAge().category();
        Map<Identifier, AgeEntry> categoryAges = this.categories.getOrDefault(category, new Object2ObjectOpenHashMap<>());

        categoryAges.remove(id);
        this.categories.put(category, categoryAges);
        this.ages.remove(id);

        LOGGER.info("Forgot about age {}", ageEntry);
    }

    public void addAll(Collection<AgeEntry> ages) {
        List<AgeEntry> list = new ArrayList<>(ages);

        while (!list.isEmpty()) {
            if (!list.removeIf(this::tryAdd)) {
                LOGGER.error("Couldn't load ages: {}", list);
                break;
            }
        }

        LOGGER.info("Loaded {} ages", this.ages.size());
    }

    public void removeAll(Set<Identifier> ages) {
        for (Identifier identifier : ages) {
            AgeEntry ageEntry = this.ages.get(identifier);

            if (ageEntry == null) {
                LOGGER.warn("Told to remove age {} but I don't know what that is", identifier);
            } else {
                this.remove(ageEntry);
            }
        }
    }

    protected void clear() {
        this.ages.clear();
        this.categories.clear();
    }
}

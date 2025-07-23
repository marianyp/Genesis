package dev.mariany.genesis.age;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.*;

public class AgeManager {
    private static final AgeManager INSTANCE = new AgeManager();

    private final Map<Identifier, AgeEntry> ages = new Object2ObjectOpenHashMap<>();

    public static AgeManager getInstance() {
        return INSTANCE;
    }

    public boolean isAgeGuarded(ItemConvertible item) {
        return this.ages.values()
                .stream()
                .anyMatch(
                        ageEntry -> ageEntry.getAge().items()
                                .stream()
                                .anyMatch(ingredient -> ingredient.test(item.asItem().getDefaultStack()))
                );
    }

    public boolean isUnlocked(ServerPlayerEntity player, RegistryKey<World> worldRegistryKey) {
        return allUnlocked(player, getRequiredAges(worldRegistryKey));
    }

    public boolean isUnlocked(ServerPlayerEntity player, Block block) {
        return isUnlocked(player, block.asItem().getDefaultStack());
    }

    public boolean isUnlocked(ServerPlayerEntity player, ItemStack stack) {
        return allUnlocked(player, getRequiredAges(stack));
    }

    public boolean allUnlocked(ServerPlayerEntity player, Collection<AgeEntry> ages) {
        if (player.isCreative()) {
            return true;
        }

        if (ages.isEmpty()) {
            return true;
        }

        return ages.stream().allMatch(placedAge -> isDoneRecursively(placedAge, player));
    }

    public boolean isDoneRecursively(AgeEntry ageEntry, ServerPlayerEntity player) {
        if (!ageEntry.getAge().requiresParent()) {
            return ageEntry.isDone(player);
        }

        while (ageEntry.isDone(player)) {
            Optional<Identifier> parentId = ageEntry.getAge().parent();

            if (parentId.isPresent()) {
                AgeEntry parentEntry = this.ages.get(parentId.get());

                if (parentEntry != null) {
                    ageEntry = parentEntry;
                    continue;
                }
            }

            return true;
        }

        return false;
    }

    public List<AgeEntry> getRequiredAges(ItemStack stack) {
        List<AgeEntry> requiredAges = new ArrayList<>();

        for (AgeEntry placedAge : this.ages.values()) {
            List<Ingredient> itemUnlocks = placedAge.getAge().items();

            for (Ingredient ingredient : itemUnlocks) {
                if (ingredient.test(stack)) {
                    requiredAges.add(placedAge);
                    break;
                }
            }
        }

        return requiredAges;
    }

    public List<AgeEntry> getRequiredAges(RegistryKey<World> worldRegistryKey) {
        List<AgeEntry> requiredAges = new ArrayList<>();

        for (AgeEntry placedAge : this.ages.values()) {
            List<RegistryKey<World>> dimensions = placedAge.getAge().dimensions();

            for (RegistryKey<World> dimension : dimensions) {
                if (worldRegistryKey.getValue().equals(dimension.getValue())) {
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

    public List<Ingredient> getAllItemUnlocks(ServerPlayerEntity player) {
        return getAges()
                .stream()
                .filter(ageEntry -> !ageEntry.isDone(player))
                .flatMap(ageEntry -> ageEntry.getAge().items().stream())
                .toList();
    }

    protected void add(AgeEntry age) {
        this.ages.put(age.getId(), age);
    }

    protected void clear() {
        this.ages.clear();
    }
}

package dev.mariany.genesis.age;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mariany.genesis.advancement.criterion.CompleteTrialSpawnerCriteria;
import dev.mariany.genesis.advancement.criterion.ObtainAdvancementCriterion;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.*;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public record Age(
        List<Ingredient> items,
        List<RegistryKey<World>> dimensions,
        Optional<Identifier> parent,
        boolean requiresParent,
        Map<String, AdvancementCriterion<?>> criteria,
        AdvancementRequirements requirements,
        AgeDisplay display
) {
    private static final Codec<Map<String, AdvancementCriterion<?>>> CRITERIA_CODEC = Codec.unboundedMap(Codec.STRING, AdvancementCriterion.CODEC);
    public static final Codec<Age> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Ingredient.CODEC.listOf().optionalFieldOf("items", List.of()).forGetter(Age::items),
                            RegistryKey.createCodec(RegistryKeys.WORLD).listOf().optionalFieldOf("dimensions", List.of())
                                    .forGetter(Age::dimensions),
                            Identifier.CODEC.optionalFieldOf("parent").forGetter(Age::parent),
                            Codec.BOOL.optionalFieldOf("requires_parent", true)
                                    .forGetter(Age::requiresParent),
                            CRITERIA_CODEC.optionalFieldOf("criteria", new HashMap<>()).forGetter(Age::criteria),
                            AdvancementRequirements.CODEC.optionalFieldOf("requirements", AdvancementRequirements.EMPTY)
                                    .forGetter(Age::requirements),
                            AgeDisplay.CODEC.fieldOf("display").forGetter(Age::display)
                    )
                    .apply(instance, Age::new)
    );

    public static class Builder {
        private final List<Ingredient> items = new ArrayList<>();
        private final List<RegistryKey<World>> dimensions = new ArrayList<>();
        @Nullable
        private Identifier parent = null;
        private boolean requiresParent = true;
        private final Map<String, AdvancementCriterion<?>> criteria = new HashMap<>();
        private AdvancementRequirements requirements = AdvancementRequirements.EMPTY;
        private AgeDisplay display;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder itemUnlocks(Ingredient ingredient) {
            this.items.add(ingredient);
            return this;
        }

        public Builder itemUnlocks(List<Ingredient> ingredients) {
            this.items.clear();
            this.items.addAll(ingredients);
            return this;
        }

        public Builder dimensionUnlocks(RegistryKey<World> worldRegistryKey) {
            this.dimensions.add(worldRegistryKey);
            return this;
        }

        public Builder dimensionUnlocks(List<RegistryKey<World>> worldRegistryKeys) {
            this.dimensions.clear();
            this.dimensions.addAll(worldRegistryKeys);
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

        public Builder requirements(AdvancementRequirements advancementRequirements) {
            this.requirements = advancementRequirements;
            return this;
        }

        public Builder criterion(String name, AdvancementCriterion<?> criterion) {
            this.criteria.put(name, criterion);
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

        public Builder requireKill(
                RegistryWrapper.Impl<EntityType<?>> entityLookup,
                EntityType<?> entityType,
                int atLeast
        ) {
            return criterion("killed_" + atLeast + EntityType.getId(entityType).getPath(),
                    OnKilledCriterion.Conditions.createPlayerKilledEntity(
                            EntityPredicate.Builder.create().type(entityLookup, entityType),
                            DamageSourcePredicate.Builder.create().sourceEntity(
                                    EntityPredicate.Builder.create().typeSpecific(
                                            PlayerPredicate.Builder.create().stat(
                                                    Stats.KILLED,
                                                    entityType.getRegistryEntry(),
                                                    NumberRange.IntRange.atLeast(atLeast - 1)).build()
                                    )
                            )
                    )
            );
        }

        public Builder requireTrialWearing(
                RegistryWrapper.Impl<Item> itemLookup,
                boolean ominous,
                Item head,
                Item chest,
                Item legs,
                Item feet
        ) {
            List<String> requirements = new ArrayList<>();

            for (EquipmentSlot slot : EquipmentSlot.values()) {
                @Nullable EntityEquipmentPredicate entityEquipmentPredicate = switch (slot) {
                    case HEAD -> EntityEquipmentPredicate.Builder.create().head(ItemPredicate.Builder.create()
                            .items(itemLookup, head)
                    ).build();
                    case CHEST -> EntityEquipmentPredicate.Builder.create().chest(ItemPredicate.Builder.create()
                            .items(itemLookup, chest)
                    ).build();
                    case LEGS -> EntityEquipmentPredicate.Builder.create().legs(ItemPredicate.Builder.create()
                            .items(itemLookup, legs)
                    ).build();
                    case FEET -> EntityEquipmentPredicate.Builder.create().feet(ItemPredicate.Builder.create()
                            .items(itemLookup, feet)
                    ).build();
                    default -> null;
                };

                if (entityEquipmentPredicate != null) {
                    String name = "trial_completed_with_" + slot.asString();

                    criterion(name, CompleteTrialSpawnerCriteria.Conditions.create(
                                    EntityPredicate.asLootContextPredicate(
                                            EntityPredicate.Builder.create().equipment(entityEquipmentPredicate).build()
                                    ),
                                    ominous
                            )
                    );

                    requirements.add(name);
                }
            }

            this.requirements = AdvancementRequirements.anyOf(requirements);

            return this;
        }

        public Builder requireTimePlayed(int ticks) {
            LootContextPredicate predicate = EntityPredicate.asLootContextPredicate(
                    EntityPredicate.Builder.create().typeSpecific(
                            PlayerPredicate.Builder.create().stat(
                                    Stats.CUSTOM,
                                    Registries.CUSTOM_STAT.getOrThrow(
                                            RegistryKey.of(RegistryKeys.CUSTOM_STAT, Stats.PLAY_TIME)
                                    ),
                                    NumberRange.IntRange.atLeast(ticks)).build()
                    ).build()
            );

            return criterion("time_played", Criteria.TICK.create(
                            new TickCriterion.Conditions(Optional.of(predicate))
                    )
            );
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
            return new AgeEntry(id, new Age(
                    this.items,
                    this.dimensions,
                    Optional.ofNullable(this.parent),
                    this.requiresParent,
                    this.criteria,
                    this.requirements,
                    this.display
            ));
        }

        public void build(Consumer<AgeEntry> exporter, Identifier id) {
            AgeEntry ageEntry = this.build(id);
            exporter.accept(ageEntry);
        }
    }
}

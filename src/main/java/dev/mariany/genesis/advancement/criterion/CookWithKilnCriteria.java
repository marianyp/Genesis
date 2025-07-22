package dev.mariany.genesis.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CookWithKilnCriteria extends AbstractCriterion<CookWithKilnCriteria.Conditions> {
    @Override
    public Codec<CookWithKilnCriteria.Conditions> getConditionsCodec() {
        return CookWithKilnCriteria.Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, ItemStack output) {
        this.trigger(player, conditions -> conditions.matches(output));
    }

    public record Conditions(Optional<LootContextPredicate> player, Optional<Ingredient> ingredient)
            implements AbstractCriterion.Conditions {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player),
                                Ingredient.CODEC.optionalFieldOf("item").forGetter(Conditions::ingredient)
                        )
                        .apply(instance, Conditions::new)
        );

        public static AdvancementCriterion<Conditions> create(ItemConvertible item) {
            return create(null, Ingredient.ofItem(item));
        }

        public static AdvancementCriterion<Conditions> create(ItemConvertible... items) {
            return create(null, Ingredient.ofItems(items));
        }

        public static AdvancementCriterion<Conditions> create(RegistryEntryList<Item> tag) {
            return create(null, Ingredient.ofTag(tag));
        }

        public static AdvancementCriterion<Conditions> create(Ingredient ingredient) {
            return create(null, ingredient);
        }

        public static AdvancementCriterion<Conditions> create(@Nullable LootContextPredicate playerPredicate, Ingredient ingredient) {
            return GenesisCriteria.COOK_WITH_KILN.create(new Conditions(Optional.ofNullable(playerPredicate), Optional.of(ingredient)));
        }

        public static AdvancementCriterion<Conditions> create() {
            return GenesisCriteria.COOK_WITH_KILN.create(new Conditions(Optional.empty(), Optional.empty()));
        }

        public boolean matches(ItemStack stack) {
            return ingredient.map(item -> item.test(stack)).orElse(true);
        }
    }
}

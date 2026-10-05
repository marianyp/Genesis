package dev.mariany.genesis.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CookWithKilnTrigger extends SimpleCriterionTrigger<CookWithKilnTrigger.Conditions> {
    @Override
    public Codec<CookWithKilnTrigger.Conditions> codec() {
        return CookWithKilnTrigger.Conditions.CODEC;
    }

    public void trigger(ServerPlayer player, ItemStack output) {
        this.trigger(player, conditions -> conditions.matches(output));
    }

    public record Conditions(Optional<Holder<LootItemCondition>> player, Optional<Ingredient> ingredient)
            implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                            LootItemCondition.CODEC
                                                    .optionalFieldOf("player")
                                                    .forGetter(Conditions::player),
                                            Ingredient.CODEC
                                                    .optionalFieldOf("item")
                                                    .forGetter(Conditions::ingredient)
                                    )
                                    .apply(instance, Conditions::new)
        );

        public static Criterion<Conditions> create(ItemLike item) {
            return create(null, Ingredient.of(item));
        }

        public static Criterion<Conditions> create(ItemLike... items) {
            return create(null, Ingredient.of(items));
        }

        public static Criterion<Conditions> create(HolderSet<Item> tag) {
            return create(null, Ingredient.of(tag));
        }

        public static Criterion<Conditions> create(Ingredient ingredient) {
            return create(null, ingredient);
        }

        public static Criterion<Conditions> create(
                @Nullable Holder<LootItemCondition> playerPredicate,
                Ingredient ingredient
        ) {
            return GenesisCriteria.COOK_WITH_KILN.createCriterion(
                    new Conditions(Optional.ofNullable(playerPredicate), Optional.of(ingredient))
            );
        }

        public static Criterion<Conditions> create() {
            return GenesisCriteria.COOK_WITH_KILN.createCriterion(
                    new Conditions(Optional.empty(), Optional.empty())
            );
        }

        public boolean matches(ItemStack stack) {
            return ingredient.map(item -> item.test(stack)).orElse(true);
        }
    }
}

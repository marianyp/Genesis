package dev.mariany.genesis.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class ItemBrokenCriterion extends SimpleCriterionTrigger<ItemBrokenCriterion.Conditions> {
    @Override
    public Codec<ItemBrokenCriterion.Conditions> codec() {
        return ItemBrokenCriterion.Conditions.CODEC;
    }

    public void trigger(ServerPlayer player, ItemStack stack) {
        this.trigger(player, conditions -> conditions.matches(stack));
    }

    public record Conditions(Optional<ContextAwarePredicate> player, Ingredient ingredient)
            implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<ItemBrokenCriterion.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(ItemBrokenCriterion.Conditions::player),
                                Ingredient.CODEC.fieldOf("item").forGetter(ItemBrokenCriterion.Conditions::ingredient)
                        )
                        .apply(instance, ItemBrokenCriterion.Conditions::new)
        );

        public static Criterion<ItemBrokenCriterion.Conditions> create(ItemLike item) {
            return create(null, Ingredient.of(item));
        }

        public static Criterion<ItemBrokenCriterion.Conditions> create(ItemLike... items) {
            return create(null, Ingredient.of(items));
        }

        public static Criterion<ItemBrokenCriterion.Conditions> create(HolderSet<Item> tag) {
            return create(null, Ingredient.of(tag));
        }

        public static Criterion<ItemBrokenCriterion.Conditions> create(Ingredient ingredient) {
            return create(null, ingredient);
        }

        public static Criterion<ItemBrokenCriterion.Conditions> create(@Nullable ContextAwarePredicate playerPredicate, Ingredient ingredient) {
            return GenesisCriteria.ITEM_BROKEN.createCriterion(new ItemBrokenCriterion.Conditions(Optional.ofNullable(playerPredicate), ingredient));
        }

        public boolean matches(ItemStack stack) {
            return this.ingredient.test(stack);
        }
    }

}

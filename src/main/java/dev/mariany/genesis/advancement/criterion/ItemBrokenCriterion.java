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

public class ItemBrokenCriterion extends AbstractCriterion<ItemBrokenCriterion.Conditions> {
    @Override
    public Codec<ItemBrokenCriterion.Conditions> getConditionsCodec() {
        return ItemBrokenCriterion.Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, ItemStack stack) {
        this.trigger(player, conditions -> conditions.matches(stack));
    }

    public record Conditions(Optional<LootContextPredicate> player, Ingredient ingredient)
            implements AbstractCriterion.Conditions {
        public static final Codec<ItemBrokenCriterion.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(ItemBrokenCriterion.Conditions::player),
                                Ingredient.CODEC.fieldOf("item").forGetter(ItemBrokenCriterion.Conditions::ingredient)
                        )
                        .apply(instance, ItemBrokenCriterion.Conditions::new)
        );

        public static AdvancementCriterion<ItemBrokenCriterion.Conditions> create(ItemConvertible item) {
            return create(null, Ingredient.ofItem(item));
        }

        public static AdvancementCriterion<ItemBrokenCriterion.Conditions> create(ItemConvertible... items) {
            return create(null, Ingredient.ofItems(items));
        }

        public static AdvancementCriterion<ItemBrokenCriterion.Conditions> create(RegistryEntryList<Item> tag) {
            return create(null, Ingredient.ofTag(tag));
        }

        public static AdvancementCriterion<ItemBrokenCriterion.Conditions> create(Ingredient ingredient) {
            return create(null, ingredient);
        }

        public static AdvancementCriterion<ItemBrokenCriterion.Conditions> create(@Nullable LootContextPredicate playerPredicate, Ingredient ingredient) {
            return GenesisCriteria.ITEM_BROKEN.create(new ItemBrokenCriterion.Conditions(Optional.ofNullable(playerPredicate), ingredient));
        }

        public boolean matches(ItemStack stack) {
            return this.ingredient.test(stack);
        }
    }

}

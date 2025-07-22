package dev.mariany.genesis.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.block.Block;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class BrushPrimitiveCauldronCriteria extends AbstractCriterion<BrushPrimitiveCauldronCriteria.Conditions> {
    @Override
    public Codec<BrushPrimitiveCauldronCriteria.Conditions> getConditionsCodec() {
        return BrushPrimitiveCauldronCriteria.Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, Block cauldron) {
        this.trigger(player, conditions -> conditions.matches(cauldron));
    }

    public record Conditions(Optional<LootContextPredicate> player, List<Block> whitelist)
            implements AbstractCriterion.Conditions {
        public static final Codec<BrushPrimitiveCauldronCriteria.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC
                                        .optionalFieldOf("player")
                                        .forGetter(BrushPrimitiveCauldronCriteria.Conditions::player),
                                Block.CODEC.codec().listOf().fieldOf("whitelist")
                                        .forGetter(BrushPrimitiveCauldronCriteria.Conditions::whitelist)
                        )
                        .apply(instance, BrushPrimitiveCauldronCriteria.Conditions::new)
        );

        public static AdvancementCriterion<BrushPrimitiveCauldronCriteria.Conditions> create() {
            return create(null, List.of());
        }

        public static AdvancementCriterion<BrushPrimitiveCauldronCriteria.Conditions> create(List<Block> whitelist) {
            return create(null, whitelist);
        }

        public static AdvancementCriterion<BrushPrimitiveCauldronCriteria.Conditions> create(
                @Nullable LootContextPredicate playerPredicate,
                List<Block> whitelist
        ) {
            return GenesisCriteria.BRUSH_PRIMITIVE_CAULDRON.create(
                    new BrushPrimitiveCauldronCriteria.Conditions(Optional.ofNullable(playerPredicate), whitelist)
            );
        }

        public boolean matches(Block cauldron) {
            return whitelist.isEmpty() || whitelist.contains(cauldron);
        }
    }
}

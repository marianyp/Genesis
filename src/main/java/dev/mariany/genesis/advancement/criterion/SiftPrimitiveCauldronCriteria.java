package dev.mariany.genesis.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class SiftPrimitiveCauldronCriteria extends SimpleCriterionTrigger<SiftPrimitiveCauldronCriteria.Conditions> {
    @Override
    public Codec<SiftPrimitiveCauldronCriteria.Conditions> codec() {
        return SiftPrimitiveCauldronCriteria.Conditions.CODEC;
    }

    public void trigger(ServerPlayer player, Block cauldron) {
        this.trigger(player, conditions -> conditions.matches(cauldron));
    }

    public record Conditions(Optional<ContextAwarePredicate> player, List<Block> whitelist)
            implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<SiftPrimitiveCauldronCriteria.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.ADVANCEMENT_CODEC
                                        .optionalFieldOf("player")
                                        .forGetter(SiftPrimitiveCauldronCriteria.Conditions::player),
                                Block.CODEC.codec().listOf().fieldOf("whitelist")
                                        .forGetter(SiftPrimitiveCauldronCriteria.Conditions::whitelist)
                        )
                        .apply(instance, SiftPrimitiveCauldronCriteria.Conditions::new)
        );

        public static Criterion<SiftPrimitiveCauldronCriteria.Conditions> create() {
            return create(null, List.of());
        }

        public static Criterion<SiftPrimitiveCauldronCriteria.Conditions> create(List<Block> whitelist) {
            return create(null, whitelist);
        }

        public static Criterion<SiftPrimitiveCauldronCriteria.Conditions> create(
                @Nullable ContextAwarePredicate playerPredicate,
                List<Block> whitelist
        ) {
            return GenesisCriteria.SIFT_PRIMITIVE_CAULDRON.createCriterion(
                    new SiftPrimitiveCauldronCriteria.Conditions(Optional.ofNullable(playerPredicate), whitelist)
            );
        }

        public boolean matches(Block cauldron) {
            return whitelist.isEmpty() || whitelist.contains(cauldron);
        }
    }
}

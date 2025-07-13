package dev.mariany.genesis.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CompleteTrialSpawnerCriteria extends AbstractCriterion<CompleteTrialSpawnerCriteria.Conditions> {
    @Override
    public Codec<CompleteTrialSpawnerCriteria.Conditions> getConditionsCodec() {
        return CompleteTrialSpawnerCriteria.Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, boolean ominous) {
        this.trigger(player, conditions -> conditions.matches(ominous));
    }

    public record Conditions(Optional<LootContextPredicate> player, boolean expectOminous)
            implements AbstractCriterion.Conditions {
        public static final Codec<CompleteTrialSpawnerCriteria.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC
                                        .optionalFieldOf("player")
                                        .forGetter(CompleteTrialSpawnerCriteria.Conditions::player),
                                Codec.BOOL.fieldOf("ominous")
                                        .forGetter(CompleteTrialSpawnerCriteria.Conditions::expectOminous)
                        )
                        .apply(instance, CompleteTrialSpawnerCriteria.Conditions::new)
        );

        public static AdvancementCriterion<CompleteTrialSpawnerCriteria.Conditions> create(boolean ominous) {
            return create(null, ominous);
        }

        public static AdvancementCriterion<CompleteTrialSpawnerCriteria.Conditions> create(
                @Nullable LootContextPredicate playerPredicate,
                boolean ominous
        ) {
            return GenesisCriteria.COMPLETE_TRIAL_SPAWNER_ADVANCEMENT.create(
                    new CompleteTrialSpawnerCriteria.Conditions(Optional.ofNullable(playerPredicate), ominous)
            );
        }

        public boolean matches(boolean ominous) {
            return ominous == expectOminous;
        }
    }
}

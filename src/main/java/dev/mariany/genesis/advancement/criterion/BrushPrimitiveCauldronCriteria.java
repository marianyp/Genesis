package dev.mariany.genesis.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;

public class BrushPrimitiveCauldronCriteria extends SimpleCriterionTrigger<BrushPrimitiveCauldronCriteria.Conditions> {
    @Override
    public Codec<BrushPrimitiveCauldronCriteria.Conditions> codec() {
        return BrushPrimitiveCauldronCriteria.Conditions.CODEC;
    }

    public void trigger(ServerPlayer player, Block cauldron) {
        this.trigger(player, conditions -> conditions.matches(cauldron));
    }

    public record Conditions(Optional<ContextAwarePredicate> player, List<Block> whitelist)
            implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<BrushPrimitiveCauldronCriteria.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                EntityPredicate.ADVANCEMENT_CODEC
                                        .optionalFieldOf("player")
                                        .forGetter(BrushPrimitiveCauldronCriteria.Conditions::player),
                                Block.CODEC.codec().listOf().fieldOf("whitelist")
                                        .forGetter(BrushPrimitiveCauldronCriteria.Conditions::whitelist)
                        )
                        .apply(instance, BrushPrimitiveCauldronCriteria.Conditions::new)
        );

        public static Criterion<BrushPrimitiveCauldronCriteria.Conditions> create() {
            return create(null, List.of());
        }

        public static Criterion<BrushPrimitiveCauldronCriteria.Conditions> create(List<Block> whitelist) {
            return create(null, whitelist);
        }

        public static Criterion<BrushPrimitiveCauldronCriteria.Conditions> create(
                @Nullable ContextAwarePredicate playerPredicate,
                List<Block> whitelist
        ) {
            return GenesisCriteria.BRUSH_PRIMITIVE_CAULDRON.createCriterion(
                    new BrushPrimitiveCauldronCriteria.Conditions(Optional.ofNullable(playerPredicate), whitelist)
            );
        }

        public boolean matches(Block cauldron) {
            return whitelist.isEmpty() || whitelist.contains(cauldron);
        }
    }
}

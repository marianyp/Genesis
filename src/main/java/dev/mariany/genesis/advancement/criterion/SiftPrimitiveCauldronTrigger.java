package dev.mariany.genesis.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class SiftPrimitiveCauldronTrigger extends SimpleCriterionTrigger<SiftPrimitiveCauldronTrigger.Conditions> {
    @Override
    public Codec<SiftPrimitiveCauldronTrigger.Conditions> codec() {
        return SiftPrimitiveCauldronTrigger.Conditions.CODEC;
    }

    public void trigger(ServerPlayer player, Block cauldron) {
        this.trigger(player, conditions -> conditions.matches(cauldron));
    }

    public record Conditions(Optional<Holder<LootItemCondition>> player, Optional<HolderSet<Block>> whitelist)
            implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<SiftPrimitiveCauldronTrigger.Conditions> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                                            LootItemCondition.CODEC
                                                    .optionalFieldOf("player")
                                                    .forGetter(SiftPrimitiveCauldronTrigger.Conditions::player),
                                            RegistryCodecs.holderSet(Registries.BLOCK)
                                                          .optionalFieldOf("whitelist")
                                                          .forGetter(SiftPrimitiveCauldronTrigger.Conditions::whitelist)
                                    )
                                    .apply(instance, SiftPrimitiveCauldronTrigger.Conditions::new)
        );

        public static Criterion<SiftPrimitiveCauldronTrigger.Conditions> create() {
            return create(null, null);
        }

        public static Criterion<SiftPrimitiveCauldronTrigger.Conditions> create(List<Block> whitelist) {
            if (whitelist.isEmpty()) {
                return create();
            }

            HolderSet<Block> blocks = HolderSet.direct(
                    whitelist.stream().map(BuiltInRegistries.BLOCK::wrapAsHolder).toList()
            );

            return create(null, blocks);
        }

        public static Criterion<SiftPrimitiveCauldronTrigger.Conditions> create(
                @Nullable Holder<LootItemCondition> playerPredicate,
                @Nullable HolderSet<Block> whitelist
        ) {
            return GenesisCriteria.SIFT_PRIMITIVE_CAULDRON.createCriterion(
                    new SiftPrimitiveCauldronTrigger.Conditions(
                            Optional.ofNullable(playerPredicate),
                            Optional.ofNullable(whitelist)
                    )
            );
        }

        public boolean matches(Block cauldron) {
            return this.whitelist.isEmpty()
                    || this.whitelist.get().contains(BuiltInRegistries.BLOCK.wrapAsHolder(cauldron));
        }
    }
}

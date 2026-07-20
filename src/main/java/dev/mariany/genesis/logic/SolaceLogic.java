package dev.mariany.genesis.logic;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.event.entity.EntityEvents;
import dev.mariany.genesis.packet.clientbound.UpdateSolaceLogicPayload;
import dev.mariany.genesis.world.effect.GenesisMobEffects;
import dev.mariany.genesis.world.level.gamerules.GenesisGameRules;
import dev.mariany.genesis.world.level.gamerules.SyncedGameRule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.stream.Stream;

public class SolaceLogic {
    private final SyncedGameRule<Double> syncedCampfireRadius = new SyncedGameRule<>(
            GenesisGameRules.SOLACE_CAMPFIRE_RADIUS,
            0D,
            UpdateSolaceLogicPayload::new
    );

    public void setCampfireRadius(double campfireRadius) {
        this.syncedCampfireRadius.setValue(campfireRadius);
    }

    public void bootstrap() {
        Genesis.bootstrapLog("Solace Logic");
        this.syncedCampfireRadius.bootstrap();
        EntityEvents.AFTER_ENTITY_CONSUME.register(this::onEntityConsume);
    }

    public boolean canEat(LivingEntity livingEntity, ItemStack stack) {
        return !livingEntity.hasEffect(GenesisMobEffects.SOLACE) && isComforting(stack) &&
                !isHealingRestricted(livingEntity);
    }

    public boolean isHealingRestricted(LivingEntity livingEntity) {
        if (isFullHealth(livingEntity) || isTakingDamage(livingEntity)) {
            return true;
        }

        return !this.isNearCampfire(livingEntity);
    }

    private static boolean isTakingDamage(LivingEntity livingEntity) {
        return livingEntity.hurtTime > 0;
    }

    private static boolean isFullHealth(LivingEntity livingEntity) {
        int health = Mth.ceil(livingEntity.getHealth());
        float maxHealth = livingEntity.getMaxHealth();
        return health >= maxHealth;
    }

    private void onEntityConsume(LivingEntity entity, ItemStack stack) {
        if (entity.level().isClientSide()) {
            return;
        }

        if (!isComforting(stack)) {
            return;
        }

        if (this.isHealingRestricted(entity)) {
            return;
        }

        MobEffectInstance effect = new MobEffectInstance(
                GenesisMobEffects.SOLACE,
                -1,
                0,
                true,
                true
        );

        entity.addEffect(effect);
    }

    private static boolean isComforting(ItemStack stack) {
        Consumable consumable = stack.get(DataComponents.CONSUMABLE);

        if (consumable == null) {
            return false;
        }

        FoodProperties foodProperties = stack.get(DataComponents.FOOD);

        if (foodProperties == null) {
            return false;
        }

        return isComforting(consumable, foodProperties);
    }

    private static boolean isComforting(Consumable consumable, FoodProperties foodProperties) {
        if (foodProperties.nutrition() <= 0) {
            return false;
        }

        return consumable.onConsumeEffects().stream().noneMatch(SolaceLogic::isHarmful);
    }

    private static boolean isHarmful(ConsumeEffect consumeEffect) {
        if (!(consumeEffect instanceof ApplyStatusEffectsConsumeEffect applyStatusEffect)) {
            return false;
        }

        return applyStatusEffect
                .effects()
                .stream()
                .anyMatch(SolaceLogic::isHarmful);
    }

    private static boolean isHarmful(MobEffectInstance mobEffectInstance) {
        return mobEffectInstance.getEffect().value().getCategory() == MobEffectCategory.HARMFUL;
    }

    private boolean isNearCampfire(Entity entity) {
        double campfireRadius = this.getCampfireRadius();

        if (campfireRadius <= 0) {
            return false;
        }

        AABB box = new AABB(BlockPos.containing(entity.position())).inflate(campfireRadius);

        Stream<BlockState> states = entity.level().getBlockStatesIfLoaded(box);

        return states.anyMatch(SolaceLogic::isCampfire);
    }

    private double getCampfireRadius() {
        return this.syncedCampfireRadius.getValue();
    }

    private static boolean isCampfire(BlockState state) {
        return state.getBlock() instanceof CampfireBlock;
    }
}

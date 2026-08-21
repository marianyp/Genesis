package dev.mariany.genesis.item.custom;

import dev.mariany.genesis.advancement.criterion.GenesisCriteria;
import dev.mariany.genesis.tag.GenesisTags;
import dev.mariany.genesis.sound.GenesisSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Collection;
import java.util.List;

public class FlintsItem extends Item {
    public FlintsItem(Properties settings) {
        super(settings);
    }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!(level instanceof ServerLevel serverLevel) || remainingUseTicks < 0 || !(user instanceof Player player)) {
            return;
        }

        if (!this.isStrikeTick(stack, user, remainingUseTicks)) {
            return;
        }

        HitResult hitResult = getHitResult(player);

        if (!(hitResult instanceof BlockHitResult blockHitResult) || hitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }

        strike(serverLevel, player, stack, blockHitResult);
    }

    private boolean isStrikeTick(ItemStack stack, LivingEntity user, int remainingUseTicks) {
        int maxUseTime = this.getUseDuration(stack, user);
        int middlePoint = maxUseTime / 20 / 2;
        int progress = maxUseTime - remainingUseTicks + 1;
        return progress == middlePoint;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 175;
    }

    private static void strike(ServerLevel level, Player player, ItemStack stack, BlockHitResult hitResult) {
        RandomSource random = player.getRandom();
        BlockPos pos = hitResult.getBlockPos();
        BlockPos abovePos = pos.above();
        BlockState state = level.getBlockState(pos);

        if (shouldLight(player, stack, random)) {
            List<ItemEntity> campfireFuel = getCampfireFuel(level, abovePos);

            boolean lit = tryLight(level, pos, abovePos, state, campfireFuel);

            if (lit) {
                onFireLit(level, player, abovePos);
            }
        }

        double smokePosition = getSmokePosition(level, pos, abovePos, state);

        playStrikeEffects(level, abovePos, smokePosition, random);

        stack.hurtAndBreak(1, player, player.getUsedItemHand());
    }

    private static boolean shouldLight(Player player, ItemStack stack, RandomSource random) {
        int damage = stack.getDamageValue();
        return player.isCreative() || damage > 0 && (random.nextBoolean() || (damage + 1) >= stack.getMaxDamage());
    }

    private static List<ItemEntity> getCampfireFuel(Level level, BlockPos pos) {
        return level.getEntitiesOfClass(
                ItemEntity.class,
                new AABB(pos),
                itemEntity -> itemEntity.isAlive() && itemEntity.getItem().is(GenesisTags.Items.CAMPFIRE_FUEL)
        );
    }

    private static boolean tryLight(
            ServerLevel level,
            BlockPos pos,
            BlockPos abovePos,
            BlockState state,
            List<ItemEntity> campfireFuel
    ) {
        if (getItemCount(campfireFuel) >= 4) {
            return tryCreateCampfire(level, abovePos, campfireFuel);
        }

        return tryLightCampfire(level, pos, state);
    }

    private static int getItemCount(Collection<ItemEntity> itemEntities) {
        return itemEntities.stream()
                     .mapToInt(stick -> stick.getItem().getCount())
                     .sum();
    }

    private static boolean tryCreateCampfire(ServerLevel level, BlockPos pos, List<ItemEntity> campfireFuel) {
        if (!level.getBlockState(pos).canBeReplaced()) {
            return false;
        }

        campfireFuel.forEach(itemEntity -> itemEntity.remove(Entity.RemovalReason.DISCARDED));

        level.setBlockAndUpdate(pos, Blocks.CAMPFIRE.defaultBlockState().setValue(CampfireBlock.LIT, true));

        return true;
    }

    private static boolean tryLightCampfire(ServerLevel level, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof CampfireBlock) || state.getValueOrElse(CampfireBlock.LIT, false)) {
            return false;
        }

        level.setBlockAndUpdate(pos, state.setValue(CampfireBlock.LIT, true));

        return true;
    }

    private static void onFireLit(ServerLevel level, Player player, BlockPos soundPos) {
        if (player instanceof ServerPlayer serverPlayer) {
            GenesisCriteria.FIRE_STARTED.trigger(serverPlayer);
        }

        level.playSound(
                null,
                soundPos,
                SoundEvents.FIRECHARGE_USE,
                SoundSource.BLOCKS,
                0.35F,
                1F
        );
    }

    private static double getSmokePosition(Level level, BlockPos pos, BlockPos abovePos, BlockState state) {
        VoxelShape shape = state.getCollisionShape(level, pos);
        AABB box = shape.bounds();
        return abovePos.getY() - 1 + box.maxY + 0.25;
    }

    private static void playStrikeEffects(ServerLevel level, BlockPos pos, double smokePosition, RandomSource random) {
        level.sendParticles(
                ParticleTypes.LAVA,
                pos.getX() + 0.5,
                smokePosition,
                pos.getZ() + 0.5,
                2,
                0,
                0,
                0,
                0.04
        );

        level.playSound(
                null,
                pos,
                GenesisSoundEvents.FLINTS,
                SoundSource.BLOCKS,
                1F,
                random.nextFloat() * 0.4F + 0.8F
        );
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BLOCK;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();

        if (player == null || getHitResult(player).getType() != HitResult.Type.BLOCK) {
            return InteractionResult.CONSUME;
        }

        player.startUsingItem(context.getHand());

        return InteractionResult.CONSUME;
    }

    private static HitResult getHitResult(Player user) {
        return ProjectileUtil.getHitResultOnViewVector(
                user,
                EntitySelector.CAN_BE_PICKED,
                user.blockInteractionRange()
        );
    }
}

package dev.mariany.genesis.item.custom;

import dev.mariany.genesis.advancement.criterion.GenesisCriteria;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class FlintsItem extends Item {
    public FlintsItem(Properties settings) {
        super(settings);
    }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (level instanceof ServerLevel serverLevel && remainingUseTicks >= 0 && user instanceof Player playerEntity) {
            RandomSource random = playerEntity.getRandom();
            HitResult hitResult = this.getHitResult(playerEntity);

            if (hitResult instanceof BlockHitResult blockHitResult && hitResult.getType() == HitResult.Type.BLOCK) {
                int maxUseTime = this.getUseDuration(stack, user);
                int middlePoint = maxUseTime / 20 / 2;
                int progress = maxUseTime - remainingUseTicks + 1;

                if (progress == middlePoint) {
                    BlockPos pos = blockHitResult.getBlockPos();
                    BlockPos abovePos = pos.above();
                    BlockState state = serverLevel.getBlockState(pos);
                    BlockState aboveState = serverLevel.getBlockState(abovePos);
                    List<ItemEntity> sticks = getSticks(serverLevel, abovePos);

                    int stickCount = sticks.stream()
                                           .map(stick -> stick.getItem().getCount())
                                           .mapToInt(Integer::intValue)
                                           .sum();

                    VoxelShape shape = state.getCollisionShape(level, pos);
                    AABB box = shape.bounds();

                    double smokePosition = abovePos.getY() - 1 + box.maxY + 0.25;
                    int damage = stack.getDamageValue();
                    boolean shouldLight = playerEntity.isCreative() ||
                            damage > 0 && (random.nextBoolean() || (damage + 1) >= stack.getMaxDamage());
                    boolean lit = false;

                    if (stickCount >= 4) {
                        if (shouldLight && aboveState.canBeReplaced()) {
                            sticks.forEach(itemEntity -> itemEntity.remove(Entity.RemovalReason.DISCARDED));
                            serverLevel.setBlockAndUpdate(
                                    abovePos,
                                    Blocks.CAMPFIRE.defaultBlockState()
                                                   .setValue(CampfireBlock.LIT, true)
                            );
                            lit = true;
                        }
                    } else if (state.getBlock() instanceof CampfireBlock) {
                        boolean isCampfireLit = state.getValueOrElse(CampfireBlock.LIT, false);

                        if (!isCampfireLit && shouldLight) {
                            serverLevel.setBlockAndUpdate(pos, state.setValue(CampfireBlock.LIT, true));
                            lit = true;
                        }
                    }

                    if (lit) {
                        if (playerEntity instanceof ServerPlayer serverPlayer) {
                            GenesisCriteria.FIRE_STARTED.trigger(serverPlayer);
                        }

                        serverLevel.playSound(
                                null,
                                abovePos,
                                SoundEvents.FIRECHARGE_USE,
                                SoundSource.BLOCKS,
                                0.35F,
                                1F
                        );
                    }

                    serverLevel.sendParticles(
                            ParticleTypes.LAVA,
                            abovePos.getX() + 0.5,
                            smokePosition,
                            abovePos.getZ() + 0.5,
                            2,
                            0,
                            0,
                            0,
                            0.04
                    );

                    serverLevel.playSound(
                            null,
                            abovePos,
                            GenesisSoundEvents.FLINTS,
                            SoundSource.BLOCKS,
                            1F,
                            random.nextFloat() * 0.4F + 0.8F
                    );

                    stack.hurtAndBreak(1, playerEntity, playerEntity.getUsedItemHand());
                }
            }
        }
    }

    private List<ItemEntity> getSticks(Level level, BlockPos pos) {
        return level.getEntitiesOfClass(
                ItemEntity.class,
                new AABB(pos),
                itemEntity -> itemEntity.isAlive() && itemEntity.getItem().getItem() == Items.STICK
        );
    }

    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 175;
    }

    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.BLOCK;
    }

    private HitResult getHitResult(Player user) {
        return ProjectileUtil.getHitResultOnViewVector(
                user,
                EntitySelector.CAN_BE_PICKED,
                user.blockInteractionRange()
        );
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player playerEntity = context.getPlayer();

        if (playerEntity != null && this.getHitResult(playerEntity).getType() == HitResult.Type.BLOCK) {
            playerEntity.startUsingItem(context.getHand());
        }

        return InteractionResult.CONSUME;
    }
}

package dev.mariany.genesis.item.custom;

import dev.mariany.genesis.sound.GenesisSoundEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

public class FlintsItem extends Item {
    public FlintsItem(Settings settings) {
        super(settings);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (world instanceof ServerWorld serverWorld && remainingUseTicks >= 0 && user instanceof PlayerEntity playerEntity) {
            Random random = playerEntity.getRandom();
            HitResult hitResult = this.getHitResult(playerEntity);
            if (hitResult instanceof BlockHitResult blockHitResult && hitResult.getType() == HitResult.Type.BLOCK) {
                int maxUseTime = this.getMaxUseTime(stack, user);
                int middlePoint = maxUseTime / 20 / 2;
                int progress = maxUseTime - remainingUseTicks + 1;

                if (progress == middlePoint) {
                    BlockPos pos = blockHitResult.getBlockPos().up();
                    List<ItemEntity> sticks = getSticks(serverWorld, pos);
                    int stickCount = sticks.stream().map(stick -> stick.getStack().getCount()).mapToInt(Integer::intValue).sum();

                    serverWorld.playSound(null, pos, GenesisSoundEvents.FLINTS, SoundCategory.BLOCKS, 1F, random.nextFloat() * 0.4F + 0.8F);
                    serverWorld.spawnParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5, 2, 0, 0, 0, 0.04);

                    if (stickCount >= 4) {
                        int damage = stack.getDamage();
                        boolean createCampfire = playerEntity.isCreative() || damage > 0 && (random.nextBoolean() || (damage + 1) >= stack.getMaxDamage());
                        if (createCampfire && serverWorld.getBlockState(pos).isReplaceable()) {
                            sticks.forEach(itemEntity -> itemEntity.remove(Entity.RemovalReason.DISCARDED));
                            serverWorld.setBlockState(pos, Blocks.CAMPFIRE.getDefaultState());
                            serverWorld.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 0.35F, 1F);
                        }
                    }

                    stack.damage(1, playerEntity, playerEntity.getActiveHand());
                }
            }
        }
    }

    private List<ItemEntity> getSticks(World world, BlockPos pos) {
        return world.getEntitiesByClass(ItemEntity.class, new Box(pos), itemEntity -> itemEntity.isAlive() && itemEntity.getStack().getItem() == Items.STICK);
    }

    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 100;
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    private HitResult getHitResult(PlayerEntity user) {
        return ProjectileUtil.getCollision(user, EntityPredicates.CAN_HIT, user.getBlockInteractionRange());
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity playerEntity = context.getPlayer();
        if (playerEntity != null && this.getHitResult(playerEntity).getType() == HitResult.Type.BLOCK) {
            playerEntity.setCurrentHand(context.getHand());
        }

        return ActionResult.CONSUME;
    }
}

package dev.mariany.genesis.entity.ai.goal;

import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Nullable;

public class DestroyCropsGoal extends MoveToTargetPosGoal {
    private final MobEntity stepAndDestroyMob;

    public DestroyCropsGoal(PathAwareEntity mob, double speed, int maxYDifference) {
        super(mob, speed, 24, maxYDifference);

        this.stepAndDestroyMob = mob;
    }

    @Override
    public boolean canStart() {
        if (!getServerWorld(this.stepAndDestroyMob).getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            return false;
        }

        return this.findTargetPos();
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void tick() {
        super.tick();

        World world = this.stepAndDestroyMob.getWorld();
        BlockPos mobPos = this.stepAndDestroyMob.getBlockPos();
        BlockPos targetBlockPos = this.tweakToProperPos(mobPos, world);

        if (this.hasReached() && targetBlockPos != null) {
            world.breakBlock(targetBlockPos, false, this.stepAndDestroyMob);
        }
    }

    @Nullable
    private BlockPos tweakToProperPos(BlockPos originalPos, BlockView world) {
        BlockPos[] positions = new BlockPos[]{
                originalPos.down(),
                originalPos.west(),
                originalPos.east(),
                originalPos.north(),
                originalPos.south()
        };

        for (BlockPos pos : positions) {
            BlockPos above = pos.up();
            BlockState state = world.getBlockState(pos);
            BlockState aboveState = world.getBlockState(above);

            if (isValidState(state)) {
                return originalPos;
            }

            if (isValidState(aboveState)) {
                return above;
            }
        }

        return null;
    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        Chunk chunk = world.getChunk(
                ChunkSectionPos.getSectionCoord(pos.getX()),
                ChunkSectionPos.getSectionCoord(pos.getZ()),
                ChunkStatus.FULL,
                false
        );

        if (chunk == null) {
            return false;
        }

        BlockPos above = pos.up();
        BlockState state = chunk.getBlockState(pos);
        BlockState aboveState = chunk.getBlockState(above);

        return isValidState(state) || isValidState(aboveState);
    }

    private boolean isValidState(BlockState state) {
        return state.getBlock() instanceof CropBlock cropBlock && cropBlock.isMature(state);
    }
}
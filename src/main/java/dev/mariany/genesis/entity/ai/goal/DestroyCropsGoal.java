package dev.mariany.genesis.entity.ai.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jetbrains.annotations.Nullable;

public class DestroyCropsGoal extends MoveToBlockGoal {
    private final Mob stepAndDestroyMob;

    public DestroyCropsGoal(PathfinderMob mob, double speed, int maxYDifference) {
        super(mob, speed, 24, maxYDifference);

        this.stepAndDestroyMob = mob;
    }

    @Override
    public double acceptedDistance() {
        return 0;
    }

    @Override
    public boolean canUse() {
        ServerLevel serverLevel = getServerLevel(this.stepAndDestroyMob);
        GameRules gameRules = serverLevel.getGameRules();

        if (!gameRules.get(GameRules.MOB_GRIEFING)) {
            return false;
        }

        return this.findNearestBlock();
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void tick() {
        super.tick();

        Level level = this.stepAndDestroyMob.level();
        BlockPos mobPos = this.stepAndDestroyMob.blockPosition();
        BlockPos cropBlockPos = this.adjustPositionToCrop(mobPos, level);

        if (cropBlockPos != null) {
            level.destroyBlock(cropBlockPos, false, this.stepAndDestroyMob);
        }
    }

    @Nullable
    private BlockPos adjustPositionToCrop(BlockPos originalPos, BlockGetter world) {
        BlockPos[] positions = new BlockPos[]{
                originalPos.below(),
                originalPos.west(),
                originalPos.east(),
                originalPos.north(),
                originalPos.south()
        };

        BlockPos position = checkPositionAndAbove(originalPos, world);

        if (position != null) {
            return position;
        }

        for (BlockPos pos : positions) {
            BlockPos iteratedPosition = checkPositionAndAbove(pos, world);

            if (iteratedPosition != null) {
                return iteratedPosition;
            }
        }

        return null;
    }

    @Nullable
    private BlockPos checkPositionAndAbove(BlockPos pos, BlockGetter world) {
        BlockPos above = pos.above();
        BlockState state = world.getBlockState(pos);
        BlockState aboveState = world.getBlockState(above);

        if (isValidState(state)) {
            return pos;
        }

        if (isValidState(aboveState)) {
            return above;
        }

        return null;
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        ChunkAccess chunk = level.getChunk(
                SectionPos.blockToSectionCoord(pos.getX()),
                SectionPos.blockToSectionCoord(pos.getZ()),
                ChunkStatus.FULL,
                false
        );

        if (chunk == null) {
            return false;
        }

        BlockPos above = pos.above();
        BlockState state = chunk.getBlockState(pos);
        BlockState aboveState = chunk.getBlockState(above);

        return isValidState(state) || isValidState(aboveState);
    }

    private boolean isValidState(BlockState state) {
        return state.getBlock() instanceof CropBlock cropBlock && cropBlock.isMaxAge(state);
    }
}

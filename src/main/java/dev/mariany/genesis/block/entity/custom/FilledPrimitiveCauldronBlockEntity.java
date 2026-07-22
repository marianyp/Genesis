package dev.mariany.genesis.block.entity.custom;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.advancement.criterion.GenesisCriteria;
import dev.mariany.genesis.block.custom.cauldron.FilledPrimitiveCauldronBlock;
import dev.mariany.genesis.block.entity.GenesisBlockEntities;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class FilledPrimitiveCauldronBlockEntity extends BlockEntity {
    private static final String BRUSHES_NBT_KEY = "brushes";
    private static final int MAX_BRUSHES = 3;
    private static final int WITHOUT_BRUSH_DELAY = 10;

    private int brushesCount;
    private long nextBrushTime;
    private ItemStack item = ItemStack.EMPTY;

    @Nullable
    private final ResourceKey<LootTable> lootTable;

    public FilledPrimitiveCauldronBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, getLootTableFromState(state));
    }

    public FilledPrimitiveCauldronBlockEntity(
            BlockPos pos,
            BlockState state,
            @Nullable ResourceKey<LootTable> lootTable
    ) {
        super(GenesisBlockEntities.FILLED_PRIMITIVE_CAULDRON, pos, state);
        this.lootTable = lootTable;
    }

    private static ResourceKey<LootTable> getLootTableFromState(BlockState state) {
        if (state.getBlock() instanceof FilledPrimitiveCauldronBlock filledPrimitiveCauldronBlock) {
            return filledPrimitiveCauldronBlock.getPrimitiveLootTable();
        }

        return null;
    }

    public boolean brush(ServerLevel level, LivingEntity brusher, ItemStack brush) {
        return brush(level, brusher, brush, false);
    }

    public boolean brush(ServerLevel level, LivingEntity brusher, ItemStack brush, boolean sound) {
        long worldTime = level.getGameTime();

        if (worldTime < this.nextBrushTime) {
            return false;
        }

        BlockState currentState = this.getBlockState();

        if (currentState.getBlock() instanceof FilledPrimitiveCauldronBlock filledPrimitiveCauldronBlock) {
            BlockState particleBlockState = filledPrimitiveCauldronBlock.getParticleBlock().defaultBlockState();
            this.addBlockBreakParticles(level, worldPosition, particleBlockState);
        }

        int delay = brush.getItem() instanceof BrushItem ? 1 : WITHOUT_BRUSH_DELAY;
        this.nextBrushTime = worldTime + delay;
        int previousDustedLevel = this.getDustedLevel();

        ++this.brushesCount;

        boolean finished = this.brushesCount >= MAX_BRUSHES;

        if (finished || sound) {
            playSound(finished);
        }

        if (finished) {
            this.finishBrushing(level, brusher, brush);
            return true;
        }

        BlockPos pos = this.getBlockPos();

        int currentDustedLevel = this.getDustedLevel();

        if (previousDustedLevel != currentDustedLevel) {
            BlockState updatedState = currentState.setValue(BlockStateProperties.DUSTED, currentDustedLevel);

            level.setBlock(pos, updatedState, Block.UPDATE_ALL);
        }

        return false;
    }

    private void playSound(boolean finished) {
        Block block = this.getBlockState().getBlock();

        if (this.level != null && block instanceof FilledPrimitiveCauldronBlock filledPrimitiveCauldronBlock) {
            SoundEvent soundEvent = finished ? filledPrimitiveCauldronBlock.getBrushCompletedSound() :
                    filledPrimitiveCauldronBlock.getBrushSound();

            this.level.playSound(null, worldPosition, soundEvent, SoundSource.BLOCKS);
        }
    }

    private void generateItem(ServerLevel level, LivingEntity brusher, ItemStack brush) {
        if (this.lootTable == null) {
            return;
        }

        LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(this.lootTable);

        LootParams lootParams = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.worldPosition))
                .withLuck(brusher.getLuck())
                .withParameter(LootContextParams.THIS_ENTITY, brusher)
                .withParameter(LootContextParams.TOOL, brush)
                .create(LootContextParamSets.ARCHAEOLOGY);

        ObjectArrayList<ItemStack> loot = lootTable.getRandomItems(lootParams, brusher.getRandom().nextLong());

        this.item = switch (loot.size()) {
            case 0 -> ItemStack.EMPTY;
            case 1 -> loot.getFirst();
            default -> {
                Genesis.LOGGER.warn(
                        "Expected max 1 loot from loot table {}, but got {}",
                        this.lootTable.identifier(),
                        loot.size()
                );
                yield loot.getFirst();
            }
        };

        this.setChanged();
    }

    private void finishBrushing(ServerLevel level, LivingEntity brusher, ItemStack brush) {
        Block baseBlock = Blocks.AIR;

        if (this.getBlockState().getBlock() instanceof BrushableBlock brushableBlock) {
            baseBlock = brushableBlock.getTurnsInto();

            if (brushableBlock instanceof FilledPrimitiveCauldronBlock filledPrimitiveCauldronBlock) {
                if (brusher instanceof ServerPlayer serverPlayer) {
                    GenesisCriteria.BRUSH_PRIMITIVE_CAULDRON.trigger(serverPlayer, filledPrimitiveCauldronBlock);
                }
            }
        }

        level.setBlock(this.worldPosition, baseBlock.defaultBlockState(), Block.UPDATE_ALL);

        this.spawnItem(level, brusher, brush);
    }

    private void spawnItem(ServerLevel level, LivingEntity brusher, ItemStack brush) {
        this.generateItem(level, brusher, brush);

        if (this.item.isEmpty()) {
            return;
        }

        double itemEntityWidth = 0.25F;
        double offsetFactor = 1.0 - itemEntityWidth;
        double halfEntityWidth = itemEntityWidth / 2.0;

        double spawnX = this.worldPosition.getX() + 0.5 * offsetFactor + halfEntityWidth;
        double spawnY = this.worldPosition.getY() + 0.5 + 0.25F / 2F;
        double spawnZ = this.worldPosition.getZ() + 0.5 * offsetFactor + halfEntityWidth;

        int stackSize = level.getRandom().nextInt(21) + 10;
        ItemStack splitStack = this.item.split(stackSize);

        ItemEntity droppedItem = new ItemEntity(level, spawnX, spawnY, spawnZ, splitStack);
        droppedItem.setDeltaMovement(Vec3.ZERO);
        level.addFreshEntity(droppedItem);

        this.item = ItemStack.EMPTY;
    }

    private void addBlockBreakParticles(ServerLevel level, BlockPos pos, BlockState state) {
        if (!state.isAir() && state.shouldSpawnTerrainParticles()) {
            level.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, state),
                    pos.getX() + 0.5,
                    pos.getY() + 0.7,
                    pos.getZ() + 0.5,
                    15,
                    0.0,
                    0.0,
                    0.0,
                    0.0
            );
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag nbtCompound = super.getUpdateTag(registries);

        nbtCompound.putInt(BRUSHES_NBT_KEY, this.brushesCount);

        return nbtCompound;
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);
        this.brushesCount = view.read(BRUSHES_NBT_KEY, ExtraCodecs.NON_NEGATIVE_INT).orElse(0);
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);

        view.store(BRUSHES_NBT_KEY, ExtraCodecs.NON_NEGATIVE_INT, this.brushesCount);
    }

    private int getDustedLevel() {
        return this.brushesCount;
    }

    public ItemStack getItem() {
        return this.item;
    }
}

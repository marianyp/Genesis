package dev.mariany.genesis.block.entity.custom;

import dev.mariany.genesis.Genesis;
import dev.mariany.genesis.block.custom.cauldron.FilledPrimitiveCauldronBlock;
import dev.mariany.genesis.block.entity.GenesisBlockEntities;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BrushableBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BrushItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Properties;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class FilledPrimitiveCauldronBlockEntity extends BlockEntity {
    private static final String BRUSHES_NBT_KEY = "brushes";
    private static final int BRUSH_DELAY = 1;
    private static final int MAX_BRUSHES = 10;

    private int brushesCount;
    private long nextBrushTime;
    private ItemStack item = ItemStack.EMPTY;

    @Nullable
    private final RegistryKey<LootTable> lootTable;

    public FilledPrimitiveCauldronBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, getLootTableFromState(state));
    }

    public FilledPrimitiveCauldronBlockEntity(BlockPos pos, BlockState state, @Nullable RegistryKey<LootTable> lootTable) {
        super(GenesisBlockEntities.FILLED_PRIMITIVE_CAULDRON, pos, state);
        this.lootTable = lootTable;
    }

    private static RegistryKey<LootTable> getLootTableFromState(BlockState state) {
        if (state.getBlock() instanceof FilledPrimitiveCauldronBlock filledPrimitiveCauldronBlock) {
            return filledPrimitiveCauldronBlock.getLootTable();
        }

        return null;
    }

    public boolean brush(ServerWorld world, LivingEntity brusher, ItemStack brush) {
        return brush(world, brusher, brush, false);
    }

    public boolean brush(ServerWorld world, LivingEntity brusher, ItemStack brush, boolean sound) {
        long worldTime = world.getTime();

        if (worldTime < this.nextBrushTime) {
            return false;
        }

        BlockState currentState = this.getCachedState();

        if (currentState.getBlock() instanceof FilledPrimitiveCauldronBlock filledPrimitiveCauldronBlock) {
            BlockState particleBlockState = filledPrimitiveCauldronBlock.getParticleBlock().getDefaultState();
            this.addBlockBreakParticles(world, pos, particleBlockState);
        }

        int delay = brush.getItem() instanceof BrushItem ? BRUSH_DELAY : BRUSH_DELAY * 10;
        this.nextBrushTime = worldTime + delay;
        int previousDustedLevel = this.getDustedLevel();

        boolean finished = ++this.brushesCount >= MAX_BRUSHES;

        if (finished || sound) {
            playSound(finished);
        }

        if (finished) {
            this.finishBrushing(world, brusher, brush);
            return true;
        }

        BlockPos pos = this.getPos();

        int currentDustedLevel = this.getDustedLevel();

        if (previousDustedLevel != currentDustedLevel) {
            BlockState updatedState = currentState.with(Properties.DUSTED, currentDustedLevel);

            world.setBlockState(pos, updatedState, Block.NOTIFY_ALL);
        }

        return false;
    }

    private void playSound(boolean finished) {
        Block block = this.getCachedState().getBlock();

        if (
                this.world != null && block instanceof FilledPrimitiveCauldronBlock filledPrimitiveCauldronBlock
        ) {
            SoundEvent soundEvent = finished ? filledPrimitiveCauldronBlock.getBrushingCompleteSound() :
                    filledPrimitiveCauldronBlock.getBrushingSound();
            this.world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS);
        }
    }

    private void generateItem(ServerWorld world, LivingEntity brusher, ItemStack brush) {
        if (this.lootTable != null) {
            LootTable lootTable = world.getServer().getReloadableRegistries().getLootTable(this.lootTable);

            if (brusher instanceof ServerPlayerEntity serverPlayerEntity) {
                Criteria.PLAYER_GENERATES_CONTAINER_LOOT.trigger(serverPlayerEntity, this.lootTable);
            }

            LootWorldContext lootWorldContext = new LootWorldContext.Builder(world)
                    .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(this.pos))
                    .luck(brusher.getLuck())
                    .add(LootContextParameters.THIS_ENTITY, brusher)
                    .add(LootContextParameters.TOOL, brush)
                    .build(LootContextTypes.ARCHAEOLOGY);
            ObjectArrayList<ItemStack> loot = lootTable.generateLoot(lootWorldContext, brusher.getRandom().nextLong());

            this.item = switch (loot.size()) {
                case 0 -> ItemStack.EMPTY;
                case 1 -> loot.getFirst();
                default -> {
                    Genesis.LOGGER.warn("Expected max 1 loot from loot table {}, but got {}", this.lootTable.getValue(), loot.size());
                    yield loot.getFirst();
                }
            };

            this.markDirty();
        }
    }

    private void finishBrushing(ServerWorld world, LivingEntity brusher, ItemStack brush) {
        Block baseBlock = Blocks.AIR;

        if (this.getCachedState().getBlock() instanceof BrushableBlock brushableBlock) {
            baseBlock = brushableBlock.getBaseBlock();
        }

        world.setBlockState(this.pos, baseBlock.getDefaultState(), Block.NOTIFY_ALL);
        this.spawnItem(world, brusher, brush);
    }

    private void spawnItem(ServerWorld world, LivingEntity brusher, ItemStack brush) {
        this.generateItem(world, brusher, brush);

        if (!this.item.isEmpty()) {
            double itemEntityWidth = EntityType.ITEM.getWidth();
            double offsetFactor = 1.0 - itemEntityWidth;
            double halfEntityWidth = itemEntityWidth / 2.0;

            double spawnX = this.pos.getX() + 0.5 * offsetFactor + halfEntityWidth;
            double spawnY = this.pos.getY() + 0.5 + EntityType.ITEM.getHeight() / 2F;
            double spawnZ = this.pos.getZ() + 0.5 * offsetFactor + halfEntityWidth;

            int stackSize = world.random.nextInt(21) + 10;
            ItemStack splitStack = this.item.split(stackSize);

            ItemEntity droppedItem = new ItemEntity(world, spawnX, spawnY, spawnZ, splitStack);
            droppedItem.setVelocity(Vec3d.ZERO);
            world.spawnEntity(droppedItem);

            this.item = ItemStack.EMPTY;
        }
    }

    private void addBlockBreakParticles(ServerWorld world, BlockPos pos, BlockState state) {
        if (!state.isAir() && state.hasBlockBreakParticles()) {
            world.spawnParticles(
                    new BlockStateParticleEffect(ParticleTypes.BLOCK, state),
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
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        NbtCompound nbtCompound = super.toInitialChunkDataNbt(registries);

        nbtCompound.putInt(BRUSHES_NBT_KEY, this.brushesCount);

        return nbtCompound;
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);

        this.brushesCount = view.read(BRUSHES_NBT_KEY, Codecs.NON_NEGATIVE_INT).orElse(0);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);

        view.put(BRUSHES_NBT_KEY, Codecs.NON_NEGATIVE_INT, this.brushesCount);
    }

    private int getDustedLevel() {
        if (this.brushesCount == 0) {
            return 0;
        }

        if (this.brushesCount < 3) {
            return 1;
        }

        return this.brushesCount < 6 ? 2 : 3;
    }

    public ItemStack getItem() {
        return this.item;
    }
}

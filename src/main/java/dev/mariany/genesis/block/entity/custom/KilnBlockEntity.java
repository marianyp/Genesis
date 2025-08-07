package dev.mariany.genesis.block.entity.custom;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import dev.mariany.genesis.block.entity.GenesisBlockEntities;
import dev.mariany.genesis.screen.KilnScreenHandler;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class KilnBlockEntity extends LockableContainerBlockEntity implements RecipeUnlocker, RecipeInputProvider, SidedInventory {
    private static final Codec<Map<RegistryKey<Recipe<?>>, Integer>> CODEC = Codec.unboundedMap(Recipe.KEY_CODEC, Codec.INT);

    private static final int DEFAULT_COOK_SECONDS = 45;
    private static final int DEFAULT_COOK_TICKS = DEFAULT_COOK_SECONDS * 20;

    private final ServerRecipeManager.MatchGetter<SingleStackRecipeInput, ? extends AbstractCookingRecipe> matchGetter;
    private final Reference2IntOpenHashMap<RegistryKey<Recipe<?>>> recipesUsed = new Reference2IntOpenHashMap<>();
    protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    int cookingTimeSpent;
    int cookingTotalTime;

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> KilnBlockEntity.this.cookingTimeSpent;
                case 1 -> KilnBlockEntity.this.cookingTotalTime;
                case 2 -> KilnBlockEntity.this.isLit() ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0:
                    KilnBlockEntity.this.cookingTimeSpent = value;
                    break;
                case 1:
                    KilnBlockEntity.this.cookingTotalTime = value;
                    break;
            }
        }

        @Override
        public int size() {
            return 3;
        }
    };

    public KilnBlockEntity(BlockPos pos, BlockState state) {
        super(GenesisBlockEntities.KILN, pos, state);
        this.matchGetter = ServerRecipeManager.createCachedMatchGetter(RecipeType.SMELTING);
    }

    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.genesis.kiln");
    }

    @Override
    protected DefaultedList<ItemStack> getHeldStacks() {
        return this.inventory;
    }

    @Override
    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new KilnScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        ItemStack previousStack = this.inventory.get(slot);
        boolean isSameItemType = !stack.isEmpty() && ItemStack.areItemsAndComponentsEqual(previousStack, stack);

        this.inventory.set(slot, stack);
        stack.capCount(this.getMaxCount(stack));

        if (slot == 0 && !isSameItemType && this.world instanceof ServerWorld serverWorld) {
            this.cookingTotalTime = getCookTime(serverWorld, this);
            this.cookingTimeSpent = 0;
            this.markDirty();
        }
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return slot == 0;
    }

    @Override
    public void provideRecipeInputs(RecipeFinder finder) {
        for (ItemStack itemStack : this.inventory) {
            finder.addInput(itemStack);
        }
    }

    @Override
    public void onBlockReplaced(BlockPos pos, BlockState oldState) {
        super.onBlockReplaced(pos, oldState);
        if (this.world instanceof ServerWorld serverWorld) {
            this.getRecipesUsedAndDropExperience(serverWorld, Vec3d.ofCenter(pos));
        }
    }

    @Override
    public void setLastRecipe(@Nullable RecipeEntry<?> recipe) {
        if (recipe != null) {
            RegistryKey<Recipe<?>> registryKey = recipe.id();
            this.recipesUsed.addTo(registryKey, 1);
        }
    }

    @Override
    public @Nullable RecipeEntry<?> getLastRecipe() {
        return null;
    }

    @Override
    public void unlockLastRecipe(PlayerEntity player, List<ItemStack> ingredients) {
    }

    public void dropExperienceForRecipesUsed(ServerPlayerEntity player) {
        List<RecipeEntry<?>> usedRecipes = this.getRecipesUsedAndDropExperience(player.getWorld(), player.getPos());
        player.unlockRecipes(usedRecipes);

        for (RecipeEntry<?> recipeEntry : usedRecipes) {
            if (recipeEntry != null) {
                player.onRecipeCrafted(recipeEntry, this.inventory);
            }
        }

        this.recipesUsed.clear();
    }

    private List<RecipeEntry<?>> getRecipesUsedAndDropExperience(ServerWorld world, Vec3d pos) {
        List<RecipeEntry<?>> usedRecipes = Lists.<RecipeEntry<?>>newArrayList();

        for (Reference2IntMap.Entry<RegistryKey<Recipe<?>>> recipeUsageEntry : this.recipesUsed.reference2IntEntrySet()) {
            world.getRecipeManager().get(recipeUsageEntry.getKey()).ifPresent(recipe -> {
                usedRecipes.add(recipe);
                dropExperience(world, pos, recipeUsageEntry.getIntValue(), ((AbstractCookingRecipe) recipe.value()).getExperience());
            });
        }

        return usedRecipes;
    }

    private static void dropExperience(ServerWorld world, Vec3d pos, int multiplier, float baseExperience) {
        float totalExperience = multiplier * baseExperience;
        int experienceToDrop = MathHelper.floor(totalExperience);
        float fractionalPart = MathHelper.fractionalPart(totalExperience);

        if (fractionalPart != 0.0F && Math.random() < fractionalPart) {
            experienceToDrop++;
        }

        ExperienceOrbEntity.spawn(world, pos, experienceToDrop);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readData(view, this.inventory);
        this.cookingTimeSpent = view.getShort("cooking_time_spent", (short) 0);
        this.cookingTotalTime = view.getShort("cooking_total_time", (short) 0);
        this.recipesUsed.clear();
        this.recipesUsed.putAll(view.read("RecipesUsed", CODEC).orElse(Map.of()));
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putShort("cooking_time_spent", (short) this.cookingTimeSpent);
        view.putShort("cooking_total_time", (short) this.cookingTotalTime);
        Inventories.writeData(view, this.inventory);
        view.put("RecipesUsed", CODEC, this.recipesUsed);
    }

    //region Prevent Hopper Usage
    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[0];
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }
    //endregion

    private boolean isLit() {
        if (this.world != null) {
            BlockState belowState = this.world.getBlockState(this.pos.down());
            Block belowBlock = belowState.getBlock();

            if (belowBlock instanceof FireBlock) {
                return true;
            }

            return belowBlock instanceof CampfireBlock && belowState.get(CampfireBlock.LIT, false);
        }

        return false;
    }

    public static void tick(ServerWorld world, BlockPos pos, BlockState state, KilnBlockEntity kiln) {
        boolean stateChanged = false;

        ItemStack inputStack = kiln.inventory.getFirst();

        boolean hasInput = !inputStack.isEmpty();

        if (kiln.isLit() && hasInput) {
            SingleStackRecipeInput recipeInput = new SingleStackRecipeInput(inputStack);
            RecipeEntry<? extends AbstractCookingRecipe> recipeEntry = kiln.matchGetter
                    .getFirstMatch(recipeInput, world)
                    .orElse(null);

            int maxStackSize = kiln.getMaxCountPerStack();

            if (
                    canAcceptRecipeOutput(
                            world.getRegistryManager(),
                            recipeEntry,
                            recipeInput,
                            kiln.inventory,
                            maxStackSize
                    )
            ) {
                kiln.cookingTimeSpent++;

                if (kiln.cookingTimeSpent >= kiln.cookingTotalTime) {
                    kiln.cookingTimeSpent = 0;
                    kiln.cookingTotalTime = getCookTime(world, kiln);

                    if (craftRecipe(world.getRegistryManager(), recipeEntry, recipeInput, kiln.inventory, maxStackSize)) {
                        kiln.setLastRecipe(recipeEntry);
                    }

                    stateChanged = true;
                }
            } else {
                kiln.cookingTimeSpent = 0;
            }
        } else if (kiln.cookingTimeSpent > 0) {
            // Cooling down if no heat source
            kiln.cookingTimeSpent = MathHelper.clamp(kiln.cookingTimeSpent - 2, 0, kiln.cookingTotalTime);
        }

        if (stateChanged) {
            markDirty(world, pos, state);
        }
    }


    private static boolean canAcceptRecipeOutput(
            DynamicRegistryManager dynamicRegistryManager,
            @Nullable RecipeEntry<? extends AbstractCookingRecipe> recipe,
            SingleStackRecipeInput input,
            DefaultedList<ItemStack> inventory,
            int maxCount
    ) {
        ItemStack inputStack = inventory.get(0);

        if (!inputStack.isEmpty() && recipe != null) {
            ItemStack itemStack = recipe.value().craft(input, dynamicRegistryManager);

            if (itemStack.isEmpty()) {
                return false;
            }

            ItemStack outputStack = inventory.get(1);

            if (outputStack.isEmpty()) {
                return true;
            }

            if (!ItemStack.areItemsAndComponentsEqual(outputStack, itemStack)) {
                return false;
            }

            return outputStack.getCount() < maxCount && outputStack.getCount() < outputStack.getMaxCount() || outputStack.getCount() < itemStack.getMaxCount();
        }

        return false;
    }

    private static boolean craftRecipe(
            DynamicRegistryManager dynamicRegistryManager,
            @Nullable RecipeEntry<? extends AbstractCookingRecipe> recipe,
            SingleStackRecipeInput input,
            DefaultedList<ItemStack> inventory,
            int maxCount
    ) {
        if (recipe != null && canAcceptRecipeOutput(dynamicRegistryManager, recipe, input, inventory, maxCount)) {
            ItemStack inputStack = inventory.get(0);
            ItemStack smeltToStack = recipe.value().craft(input, dynamicRegistryManager);
            ItemStack outputStack = inventory.get(1);

            if (outputStack.isEmpty()) {
                inventory.set(1, smeltToStack.copy());
            } else if (ItemStack.areItemsAndComponentsEqual(outputStack, smeltToStack)) {
                outputStack.increment(1);
            }

            inputStack.decrement(1);

            return true;
        }

        return false;
    }

    private static int getCookTime(ServerWorld world, KilnBlockEntity kiln) {
        SingleStackRecipeInput singleStackRecipeInput = new SingleStackRecipeInput(kiln.getStack(0));
        int cookTime = kiln.matchGetter
                .getFirstMatch(singleStackRecipeInput, world)
                .map(recipe -> recipe.value().getCookingTime())
                .orElse(DEFAULT_COOK_TICKS);

        return Math.max(cookTime, DEFAULT_COOK_TICKS);
    }
}

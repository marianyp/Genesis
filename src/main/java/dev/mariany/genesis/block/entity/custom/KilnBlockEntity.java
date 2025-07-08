package dev.mariany.genesis.block.entity.custom;

import com.mojang.serialization.Codec;
import dev.mariany.genesis.block.entity.GenesisBlockEntities;
import dev.mariany.genesis.screen.KilnScreenHandler;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.LockableContainerBlockEntity;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class KilnBlockEntity extends LockableContainerBlockEntity implements RecipeUnlocker, RecipeInputProvider, SidedInventory {
    private static final Codec<Map<RegistryKey<Recipe<?>>, Integer>> CODEC = Codec.unboundedMap(Recipe.KEY_CODEC, Codec.INT);

    private static final int DEFAULT_COOK_TIME = 800;

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
    public void provideRecipeInputs(RecipeFinder finder) {
        for (ItemStack itemStack : this.inventory) {
            finder.addInput(itemStack);
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
            return belowState.getBlock() instanceof CampfireBlock && belowState.get(CampfireBlock.LIT, false);
        }

        return false;
    }

    public static void tick(ServerWorld world, BlockPos pos, BlockState state, KilnBlockEntity furnace) {
        boolean stateChanged = false;

        ItemStack inputStack = furnace.inventory.getFirst();

        boolean hasInput = !inputStack.isEmpty();

        if (furnace.isLit() && hasInput) {
            SingleStackRecipeInput recipeInput = new SingleStackRecipeInput(inputStack);
            RecipeEntry<? extends AbstractCookingRecipe> recipeEntry = furnace.matchGetter
                    .getFirstMatch(recipeInput, world)
                    .orElse(null);

            int maxStackSize = furnace.getMaxCountPerStack();

            if (canAcceptRecipeOutput(world.getRegistryManager(), recipeEntry, recipeInput, furnace.inventory, maxStackSize)) {
                furnace.cookingTimeSpent++;

                if (furnace.cookingTimeSpent >= furnace.cookingTotalTime) {
                    furnace.cookingTimeSpent = 0;
                    furnace.cookingTotalTime = getCookTime(world, furnace);

                    if (craftRecipe(world.getRegistryManager(), recipeEntry, recipeInput, furnace.inventory, maxStackSize)) {
                        furnace.setLastRecipe(recipeEntry);
                    }

                    stateChanged = true;
                }
            } else {
                furnace.cookingTimeSpent = 0;
            }
        } else if (furnace.cookingTimeSpent > 0) {
            // Cooling down if no heat source
            furnace.cookingTimeSpent = MathHelper.clamp(furnace.cookingTimeSpent - 2, 0, furnace.cookingTotalTime);
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
                .orElse(DEFAULT_COOK_TIME);

        return Math.max(cookTime, DEFAULT_COOK_TIME);
    }
}

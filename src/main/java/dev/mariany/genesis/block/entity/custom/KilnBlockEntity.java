package dev.mariany.genesis.block.entity.custom;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import dev.mariany.genesis.block.entity.GenesisBlockEntities;
import dev.mariany.genesis.screen.KilnScreenHandler;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.world.level.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class KilnBlockEntity extends BaseContainerBlockEntity
        implements RecipeCraftingHolder, StackedContentsCompatible, WorldlyContainer {
    private static final Codec<Map<ResourceKey<Recipe<?>>, Integer>> CODEC =
            Codec.unboundedMap(Recipe.KEY_CODEC, Codec.INT);

    private static final int DEFAULT_COOK_SECONDS = 45;
    private static final int DEFAULT_COOK_TICKS = DEFAULT_COOK_SECONDS * 20;

    private final RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> matchGetter;
    private final Reference2IntOpenHashMap<ResourceKey<Recipe<?>>> recipesUsed = new Reference2IntOpenHashMap<>();

    protected NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);

    int cookingTimeSpent;
    int cookingTotalTime;

    protected final ContainerData propertyDelegate = new ContainerData() {
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
        public int getCount() {
            return 3;
        }
    };

    public KilnBlockEntity(BlockPos pos, BlockState state) {
        super(GenesisBlockEntities.KILN, pos, state);
        this.matchGetter = RecipeManager.createCheck(RecipeType.SMELTING);
    }

    @Override
    public int getContainerSize() {
        return inventory.size();
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.genesis.kiln");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.inventory;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    @Override
    protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return new KilnScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        ItemStack previousStack = this.inventory.get(slot);
        boolean isSameItemType = !stack.isEmpty() && ItemStack.isSameItemSameComponents(previousStack, stack);

        this.inventory.set(slot, stack);
        stack.limitSize(this.getMaxStackSize(stack));

        if (slot == 0 && !isSameItemType && this.level instanceof ServerLevel serverLevel) {
            this.cookingTotalTime = getCookTime(serverLevel, this);
            this.cookingTimeSpent = 0;
            this.setChanged();
        }
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot == 0;
    }

    @Override
    public void fillStackedContents(StackedItemContents finder) {
        for (ItemStack itemStack : this.inventory) {
            finder.accountStack(itemStack);
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState oldState) {
        super.preRemoveSideEffects(pos, oldState);
        if (this.level instanceof ServerLevel serverLevel) {
            this.getRecipesUsedAndDropExperience(serverLevel, Vec3.atCenterOf(pos));
        }
    }

    @Override
    public void setRecipeUsed(@Nullable RecipeHolder<?> recipe) {
        if (recipe != null) {
            ResourceKey<Recipe<?>> registryKey = recipe.id();
            this.recipesUsed.addTo(registryKey, 1);
        }
    }

    @Override
    public @Nullable RecipeHolder<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void awardUsedRecipes(Player player, List<ItemStack> ingredients) {
    }

    public void dropExperienceForRecipesUsed(ServerPlayer player) {
        List<RecipeHolder<?>> usedRecipes = this.getRecipesUsedAndDropExperience(
                player.level(),
                player.position()
        );

        player.awardRecipes(usedRecipes);

        for (RecipeHolder<?> recipeEntry : usedRecipes) {
            if (recipeEntry != null) {
                player.triggerRecipeCrafted(recipeEntry, this.inventory);
            }
        }

        this.recipesUsed.clear();
    }

    private List<RecipeHolder<?>> getRecipesUsedAndDropExperience(ServerLevel level, Vec3 pos) {
        List<RecipeHolder<?>> usedRecipes = Lists.<RecipeHolder<?>>newArrayList();

        for (Reference2IntMap.Entry<ResourceKey<Recipe<?>>> recipeUsageEntry : this.recipesUsed.reference2IntEntrySet()) {
            level.recipeAccess().byKey(recipeUsageEntry.getKey()).ifPresent(recipe -> {
                usedRecipes.add(recipe);
                dropExperience(
                        level,
                        pos,
                        recipeUsageEntry.getIntValue(),
                        ((AbstractCookingRecipe) recipe.value()).experience()
                );
            });
        }

        return usedRecipes;
    }

    private static void dropExperience(ServerLevel level, Vec3 pos, int multiplier, float baseExperience) {
        float totalExperience = multiplier * baseExperience;
        int experienceToDrop = Mth.floor(totalExperience);
        float fractionalPart = Mth.frac(totalExperience);

        if (fractionalPart != 0.0F && Math.random() < fractionalPart) {
            experienceToDrop++;
        }

        ExperienceOrb.award(level, pos, experienceToDrop);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);
        this.inventory = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(view, this.inventory);
        this.cookingTimeSpent = view.getShortOr("cooking_time_spent", (short) 0);
        this.cookingTotalTime = view.getShortOr("cooking_total_time", (short) 0);
        this.recipesUsed.clear();
        this.recipesUsed.putAll(view.read("RecipesUsed", CODEC).orElse(Map.of()));
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);
        view.putShort("cooking_time_spent", (short) this.cookingTimeSpent);
        view.putShort("cooking_total_time", (short) this.cookingTotalTime);
        ContainerHelper.saveAllItems(view, this.inventory);
        view.store("RecipesUsed", CODEC, this.recipesUsed);
    }

    //region Prevent Hopper Usage
    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return false;
    }
    //endregion

    private boolean isLit() {
        if (this.level != null) {
            BlockState belowState = this.level.getBlockState(this.worldPosition.below());
            Block belowBlock = belowState.getBlock();

            if (belowBlock instanceof BaseFireBlock) {
                return true;
            }

            return belowBlock instanceof CampfireBlock && belowState.getValueOrElse(CampfireBlock.LIT, false);
        }

        return false;
    }

    public static void tick(ServerLevel level, BlockPos pos, BlockState state, KilnBlockEntity kiln) {
        boolean stateChanged = false;

        ItemStack inputStack = kiln.inventory.getFirst();

        boolean hasInput = !inputStack.isEmpty();

        if (kiln.isLit() && hasInput) {
            SingleRecipeInput recipeInput = new SingleRecipeInput(inputStack);
            RecipeHolder<? extends AbstractCookingRecipe> recipeEntry = kiln.matchGetter
                    .getRecipeFor(recipeInput, level)
                    .orElse(null);

            int maxStackSize = kiln.getMaxStackSize();

            if (
                    canAcceptRecipeOutput(
                            level.registryAccess(),
                            recipeEntry,
                            recipeInput,
                            kiln.inventory,
                            maxStackSize
                    )
            ) {
                kiln.cookingTimeSpent++;

                if (kiln.cookingTimeSpent >= kiln.cookingTotalTime) {
                    kiln.cookingTimeSpent = 0;
                    kiln.cookingTotalTime = getCookTime(level, kiln);

                    if (craftRecipe(
                            level.registryAccess(),
                            recipeEntry,
                            recipeInput,
                            kiln.inventory,
                            maxStackSize
                    )) {
                        kiln.setRecipeUsed(recipeEntry);
                    }

                    stateChanged = true;
                }
            } else {
                kiln.cookingTimeSpent = 0;
            }
        } else if (kiln.cookingTimeSpent > 0) {
            // Cooling down if no heat source
            kiln.cookingTimeSpent = Mth.clamp(kiln.cookingTimeSpent - 2, 0, kiln.cookingTotalTime);
        }

        if (stateChanged) {
            setChanged(level, pos, state);
        }
    }


    private static boolean canAcceptRecipeOutput(
            RegistryAccess dynamicRegistryManager,
            @Nullable RecipeHolder<? extends AbstractCookingRecipe> recipe,
            SingleRecipeInput input,
            NonNullList<ItemStack> inventory,
            int maxCount
    ) {
        ItemStack inputStack = inventory.get(0);

        if (!inputStack.isEmpty() && recipe != null) {
            ItemStack itemStack = recipe.value().assemble(input);

            if (itemStack.isEmpty()) {
                return false;
            }

            ItemStack outputStack = inventory.get(1);

            if (outputStack.isEmpty()) {
                return true;
            }

            if (!ItemStack.isSameItemSameComponents(outputStack, itemStack)) {
                return false;
            }

            return outputStack.getCount() < maxCount && outputStack.getCount() < outputStack.getMaxStackSize() ||
                    outputStack.getCount() < itemStack.getMaxStackSize();
        }

        return false;
    }

    private static boolean craftRecipe(
            RegistryAccess dynamicRegistryManager,
            @Nullable RecipeHolder<? extends AbstractCookingRecipe> recipe,
            SingleRecipeInput input,
            NonNullList<ItemStack> inventory,
            int maxCount
    ) {
        if (recipe != null && canAcceptRecipeOutput(dynamicRegistryManager, recipe, input, inventory, maxCount)) {
            ItemStack inputStack = inventory.get(0);
            ItemStack smeltToStack = recipe.value().assemble(input);
            ItemStack outputStack = inventory.get(1);

            if (outputStack.isEmpty()) {
                inventory.set(1, smeltToStack.copy());
            } else if (ItemStack.isSameItemSameComponents(outputStack, smeltToStack)) {
                outputStack.grow(1);
            }

            inputStack.shrink(1);

            return true;
        }

        return false;
    }

    private static int getCookTime(ServerLevel level, KilnBlockEntity kiln) {
        SingleRecipeInput singleStackRecipeInput = new SingleRecipeInput(kiln.getItem(0));

        int cookTime = kiln.matchGetter
                .getRecipeFor(singleStackRecipeInput, level)
                .map(recipe -> recipe.value().cookingTime())
                .orElse(DEFAULT_COOK_TICKS);

        return Math.max(cookTime, DEFAULT_COOK_TICKS);
    }
}

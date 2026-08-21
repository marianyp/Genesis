package dev.mariany.genesis.block.entity.custom;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import dev.mariany.genesis.block.entity.GenesisBlockEntities;
import dev.mariany.genesis.screen.KilnScreenHandler;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class KilnBlockEntity extends SealedContainerBlockEntity
        implements RecipeCraftingHolder, StackedContentsCompatible {
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

        if (slot != 0 || isSameItemType || !(this.level instanceof ServerLevel serverLevel)) {
            return;
        }

        this.cookingTotalTime = getCookTime(serverLevel, this);
        this.cookingTimeSpent = 0;

        this.setChanged();
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

        if (!(this.level instanceof ServerLevel serverLevel)) {
            return;
        }

        this.getRecipesUsedAndDropExperience(serverLevel, Vec3.atCenterOf(pos));
    }

    @Override
    public void setRecipeUsed(@Nullable RecipeHolder<?> recipe) {
        if (recipe == null) {
            return;
        }

        this.recipesUsed.addTo(recipe.id(), 1);
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
        List<RecipeHolder<?>> usedRecipes = Lists.newArrayList();

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

    private boolean isLit() {
        if (this.level == null) {
            return false;
        }

        BlockState belowState = this.level.getBlockState(this.worldPosition.below());
        Block belowBlock = belowState.getBlock();

        if (belowBlock instanceof BaseFireBlock) {
            return true;
        }

        return belowBlock instanceof CampfireBlock && belowState.getValueOrElse(CampfireBlock.LIT, false);
    }

    public static void tick(ServerLevel level, BlockPos pos, BlockState state, KilnBlockEntity kiln) {
        ItemStack inputStack = kiln.inventory.getFirst();

        if (!kiln.isLit() || inputStack.isEmpty()) {
            coolDown(kiln);
            return;
        }

        SingleRecipeInput recipeInput = new SingleRecipeInput(inputStack);

        RecipeHolder<? extends AbstractCookingRecipe> recipeEntry = kiln.matchGetter
                .getRecipeFor(recipeInput, level)
                .orElse(null);

        int maxStackSize = kiln.getMaxStackSize();

        if (recipeEntry == null || isInvalidInput(recipeEntry, recipeInput, kiln.inventory, maxStackSize)) {
            kiln.cookingTimeSpent = 0;
            return;
        }

        kiln.cookingTimeSpent++;

        if (kiln.cookingTimeSpent < kiln.cookingTotalTime) {
            return;
        }

        kiln.cookingTimeSpent = 0;
        kiln.cookingTotalTime = getCookTime(level, kiln);

        if (craftRecipe(recipeEntry, recipeInput, kiln.inventory, maxStackSize)) {
            kiln.setRecipeUsed(recipeEntry);
        }

        setChanged(level, pos, state);
    }

    private static void coolDown(KilnBlockEntity kiln) {
        if (kiln.cookingTimeSpent <= 0) {
            return;
        }

        kiln.cookingTimeSpent = Mth.clamp(kiln.cookingTimeSpent - 2, 0, kiln.cookingTotalTime);
    }

    private static boolean isInvalidInput(
            RecipeHolder<? extends AbstractCookingRecipe> recipe,
            SingleRecipeInput input,
            NonNullList<ItemStack> inventory,
            int maxCount
    ) {
        ItemStack inputStack = inventory.getFirst();

        if (inputStack.isEmpty()) {
            return true;
        }

        ItemStack itemStack = recipe.value().assemble(input);

        if (itemStack.isEmpty()) {
            return true;
        }

        ItemStack outputStack = inventory.get(1);

        if (outputStack.isEmpty()) {
            return false;
        }

        if (!ItemStack.isSameItemSameComponents(outputStack, itemStack)) {
            return true;
        }

        return (outputStack.getCount() >= maxCount || outputStack.getCount() >= outputStack.getMaxStackSize()) &&
                outputStack.getCount() >= itemStack.getMaxStackSize();
    }

    private static boolean craftRecipe(
            @Nullable RecipeHolder<? extends AbstractCookingRecipe> recipe,
            SingleRecipeInput input,
            NonNullList<ItemStack> inventory,
            int maxCount
    ) {
        if (recipe == null || isInvalidInput(recipe, input, inventory, maxCount)) {
            return false;
        }

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

    private static int getCookTime(ServerLevel level, KilnBlockEntity kiln) {
        SingleRecipeInput singleStackRecipeInput = new SingleRecipeInput(kiln.getItem(0));

        int cookTime = kiln.matchGetter
                .getRecipeFor(singleStackRecipeInput, level)
                .map(recipe -> recipe.value().cookingTime())
                .orElse(DEFAULT_COOK_TICKS);

        return Math.max(cookTime, DEFAULT_COOK_TICKS);
    }
}

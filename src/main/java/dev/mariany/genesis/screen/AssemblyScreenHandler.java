package dev.mariany.genesis.screen;

import dev.mariany.genesis.block.GenesisBlocks;
import dev.mariany.genesis.item.custom.AssemblyPatternItem;
import dev.mariany.genesis.recipe.AssemblyRecipe;
import dev.mariany.genesis.recipe.CraftingPattern;
import dev.mariany.genesis.recipe.GenesisRecipeTypes;
import dev.mariany.genesis.screen.slot.AssemblyInputSlot;
import dev.mariany.genesis.screen.slot.AssemblyPatternSlot;
import dev.mariany.genesis.sound.GenesisSoundEvents;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AssemblyScreenHandler extends AbstractCraftingMenu {
    private final static int SIZE = 3;

    public static final int CRAFTING_SLOT_START_INDEX = 0;
    public static final int CRAFTING_SLOT_COUNT = SIZE * SIZE;
    public static final int PATTERN_SLOT_INDEX = CRAFTING_SLOT_START_INDEX + CRAFTING_SLOT_COUNT;
    public static final int PATTERN_SLOT_COUNT = 1;
    public static final int RESULT_SLOT_INDEX = PATTERN_SLOT_INDEX + PATTERN_SLOT_COUNT;
    public static final int RESULT_SLOT_COUNT = 1;
    public static final int INVENTORY_SLOT_START_INDEX = RESULT_SLOT_INDEX + RESULT_SLOT_COUNT;
    public static final int MAIN_INVENTORY_SLOT_COUNT = 27;
    public static final int HOTBAR_SLOT_COUNT = 9;
    public static final int INVENTORY_SLOT_COUNT = MAIN_INVENTORY_SLOT_COUNT + HOTBAR_SLOT_COUNT;
    public static final int HOTBAR_SLOT_START_INDEX = INVENTORY_SLOT_START_INDEX + MAIN_INVENTORY_SLOT_COUNT;

    private static final int PATTERN_CONTAINER_SLOT_INDEX = 0;
    private static final int RESULT_CONTAINER_SLOT_INDEX = 0;

    private final ContainerLevelAccess context;
    private final Player player;
    private final SimpleContainer assemblyPatternInventory = new SimpleContainer(PATTERN_SLOT_COUNT);
    private final AssemblyPatternSlot assemblyPatternSlot;
    private final ResultSlot resultSlot;
    private final List<Runnable> assemblyPatternChangeListeners = new ArrayList<>();

    private boolean filling;

    public AssemblyScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, ContainerLevelAccess.NULL);
    }

    public AssemblyScreenHandler(int syncId, Inventory playerInventory, ContainerLevelAccess context) {
        super(GenesisScreenHandlers.ASSEMBLY, syncId, SIZE, SIZE);

        this.context = context;
        this.player = playerInventory.player;

        onAssemblyPatternChange(() -> {
            this.dropLockedInputs();
            playerInventory.setChanged();
        });

        this.addCraftingGridSlots(53, 17);

        this.assemblyPatternSlot = this.addAssemblyPatternSlot();
        this.resultSlot = this.addResultSlot(this.player, 147, 35);

        this.addStandardInventorySlots(playerInventory, 8, 84);
    }

    public void onAssemblyPatternChange(Runnable listener) {
        this.assemblyPatternChangeListeners.add(listener);
    }

    private void notifyAssemblyPatternChangeListeners() {
        this.assemblyPatternChangeListeners.forEach(Runnable::run);
    }

    private static void dropOrPlaceInInventory(Player player, ItemStack stack) {
        boolean removed = player.isRemoved() && player.getRemovalReason() != Entity.RemovalReason.CHANGED_DIMENSION;

        boolean disconnected =
                player instanceof ServerPlayer serverPlayerEntity && serverPlayerEntity.hasDisconnected();

        if (removed || disconnected) {
            player.drop(stack, false);
        } else if (player instanceof ServerPlayer) {
            player.getInventory().placeItemBackInInventory(stack);
        }
    }

    protected void updateResult(ServerLevel level, @Nullable RecipeHolder<CraftingRecipe> recipe) {
        if (player instanceof ServerPlayer serverPlayer) {
            CraftingInput craftingRecipeInput = craftSlots.asCraftInput();
            ItemStack result = ItemStack.EMPTY;
            Optional<RecipeHolder<CraftingRecipe>> optionalRecipeEntry = level
                    .getServer()
                    .getRecipeManager()
                    .getRecipeFor(
                            GenesisRecipeTypes.ASSEMBLY,
                            craftingRecipeInput,
                            level,
                            recipe
                    );

            if (optionalRecipeEntry.isPresent()) {
                RecipeHolder<CraftingRecipe> recipeEntry = optionalRecipeEntry.get();
                CraftingRecipe craftingRecipe = recipeEntry.value();

                if (this.resultSlots.setRecipeUsed(serverPlayer, recipeEntry)) {
                    ItemStack stack = craftingRecipe.assemble(craftingRecipeInput);
                    boolean stackEnabled = stack.isItemEnabled(level.enabledFeatures());
                    boolean assemblyCanCraft = this.getCrafts().map(stack::is).orElse(false);

                    if (stackEnabled && assemblyCanCraft) {
                        result = stack;
                    }
                }
            }

            this.resultSlots.setItem(RESULT_CONTAINER_SLOT_INDEX, result);
            this.setRemoteSlot(RESULT_SLOT_INDEX, result);

            serverPlayer.connection.send(
                    new ClientboundContainerSetSlotPacket(
                            this.containerId,
                            this.incrementStateId(),
                            RESULT_SLOT_INDEX,
                            result
                    )
            );
        }
    }

    private void dropLockedInputs() {
        for (int i = 0; i < this.craftSlots.getContainerSize(); i++) {
            if (this.isInputSlotDisabled(i)) {
                dropOrPlaceInInventory(player, this.craftSlots.removeItemNoUpdate(i));
            }
        }

        this.getInputGridSlots().forEach(Slot::setChanged);
    }

    @Override
    protected void addCraftingGridSlots(int x, int y) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                this.addSlot(
                        new AssemblyInputSlot(
                                this,
                                this.craftSlots,
                                j + i * SIZE,
                                x + j * 18,
                                y + i * 18
                        )
                );
            }
        }
    }

    protected AssemblyPatternSlot addAssemblyPatternSlot() {
        AssemblyPatternSlot assemblyPatternSlot = new AssemblyPatternSlot(
                this,
                this.assemblyPatternInventory,
                PATTERN_CONTAINER_SLOT_INDEX,
                28,
                35
        );

        this.addSlot(assemblyPatternSlot);

        return assemblyPatternSlot;
    }

    @Override
    protected ResultSlot addResultSlot(Player player, int x, int y) {
        ResultSlot craftingResultSlot = new ResultSlot(
                player,
                this.craftSlots,
                this.resultSlots,
                RESULT_CONTAINER_SLOT_INDEX,
                x,
                y
        ) {
            @Override
            public void onTake(Player player, ItemStack stack) {
                super.onTake(player, stack);

                AssemblyScreenHandler.this.assemblyPatternInventory
                        .getItem(PATTERN_CONTAINER_SLOT_INDEX)
                        .shrink(1);
                AssemblyScreenHandler.this.assemblyPatternInventory.setChanged();

                AssemblyScreenHandler.this.notifyAssemblyPatternChangeListeners();

                if (!(player instanceof ServerPlayer serverPlayer)) {
                    return;
                }

                AssemblyScreenHandler.this.context.execute(
                        (world, pos) ->
                                serverPlayer.connection.send(
                                        new ClientboundSoundEntityPacket(
                                                GenesisSoundEvents.BLOCK_ASSEMBLY_TABLE_USE,
                                                SoundSource.BLOCKS,
                                                serverPlayer,
                                                1,
                                                Mth.randomBetween(world.getRandom(), 1.4F, 0.9F),
                                                world.getRandom().nextLong()
                                        )
                                )
                );
            }
        };

        this.addSlot(craftingResultSlot);

        return craftingResultSlot;
    }

    public Optional<AssemblyPatternItem> getAssemblyPatternItem() {
        if (this.assemblyPatternSlot.getItem().getItem() instanceof AssemblyPatternItem assemblyPatternItem) {
            return Optional.of(assemblyPatternItem);
        }

        return Optional.empty();
    }

    public Optional<CraftingPattern> getCraftingPattern() {
        return getAssemblyPatternItem().map(AssemblyPatternItem::getCraftingPattern);
    }

    public Optional<TagKey<Item>> getCrafts() {
        return getAssemblyPatternItem().map(AssemblyPatternItem::getCrafts);
    }

    public boolean isInputSlotDisabled(int slot) {
        return this.getCraftingPattern()
                   .map(craftingPattern -> craftingPattern.isSlotDisabled(slot))
                   .orElse(true);
    }

    @Override
    public Slot getResultSlot() {
        return this.resultSlot;
    }

    @Override
    public List<Slot> getInputGridSlots() {
        return this.slots.subList(
                CRAFTING_SLOT_START_INDEX,
                CRAFTING_SLOT_START_INDEX + CRAFTING_SLOT_COUNT
        );
    }

    @Override
    protected Player owner() {
        return this.player;
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return RecipeBookType.CRAFTING;
    }

    @Override
    public void slotsChanged(Container inventory) {
        if (inventory == this.assemblyPatternInventory) {
            this.notifyAssemblyPatternChangeListeners();
        }

        if (this.filling) {
            return;
        }

        this.context.execute((world, pos) -> {
            if (world instanceof ServerLevel serverLevel) {
                updateResult(serverLevel, null);
            }
        });
    }

    @Override
    public void beginPlacingRecipe() {
        this.filling = true;
    }

    @Override
    public void finishPlacingRecipe(ServerLevel level, RecipeHolder<CraftingRecipe> recipe) {
        this.filling = false;
        updateResult(level, recipe);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RecipeBookMenu.PostPlaceAction handlePlacement(
            boolean craftAll,
            boolean creative,
            RecipeHolder<?> recipe,
            ServerLevel level,
            Inventory inventory
    ) {
        List<Slot> gridSlots = this.getInputGridSlots();
        RecipeHolder<CraftingRecipe> craftingRecipe = (RecipeHolder<CraftingRecipe>) recipe;

        this.beginPlacingRecipe();

        try {
            return ServerPlaceRecipe.placeRecipe(
                    new ServerPlaceRecipe.CraftingMenuAccess<>() {
                        @Override
                        public void fillCraftSlotsStackedContents(StackedItemContents finder) {
                            AssemblyScreenHandler.this.fillCraftSlotsStackedContents(finder);
                        }

                        @Override
                        public void clearCraftingContent() {
                            for (Slot slot : gridSlots) {
                                slot.set(ItemStack.EMPTY);
                            }
                        }

                        @Override
                        public boolean recipeMatches(RecipeHolder<CraftingRecipe> recipeHolder) {
                            return false;
                        }
                    },
                    AssemblyScreenHandler.this.getGridWidth(),
                    AssemblyScreenHandler.this.getGridHeight(),
                    gridSlots,
                    gridSlots,
                    inventory,
                    craftingRecipe,
                    false,
                    creative
            );
        } finally {
            this.finishPlacingRecipe(level, craftingRecipe);
        }
    }

    public void quickCraft(RecipeHolder<AssemblyRecipe> recipe) {
        if (!(this.player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        Inventory inventory = serverPlayer.getInventory();
        ItemStack currentPattern = this.assemblyPatternSlot.getItem();

        if (isValidPattern(currentPattern, recipe)) {
            this.handlePlacement(
                    false,
                    serverPlayer.hasInfiniteMaterials(),
                    recipe,
                    serverPlayer.level(),
                    inventory
            );

            return;
        }

        int patternInventorySlot = findPatternInventorySlot(inventory, recipe);

        if (patternInventorySlot == Inventory.NOT_FOUND_INDEX) {
            return;
        }

        ItemStack newPattern = inventory.removeItem(patternInventorySlot, 1);
        StackedItemContents availableItems = new StackedItemContents();

        inventory.fillStackedContents(availableItems);
        this.fillCraftSlotsStackedContents(availableItems);

        if (!availableItems.canCraft(recipe.value(), null)) {
            restoreInventoryItem(inventory, patternInventorySlot, newPattern);
            return;
        }

        ItemStack oldPattern = this.assemblyPatternInventory.getItem(PATTERN_CONTAINER_SLOT_INDEX);
        if (!canFitPatternChange(inventory, oldPattern, newPattern)) {
            restoreInventoryItem(inventory, patternInventorySlot, newPattern);
            return;
        }

        this.assemblyPatternInventory.removeItemNoUpdate(PATTERN_CONTAINER_SLOT_INDEX);

        this.assemblyPatternSlot.set(newPattern);

        dropOrPlaceInInventory(serverPlayer, oldPattern);

        this.handlePlacement(
                false,
                serverPlayer.hasInfiniteMaterials(),
                recipe,
                serverPlayer.level(),
                inventory
        );
    }

    private static boolean isValidPattern(ItemStack stack, RecipeHolder<AssemblyRecipe> recipe) {
        return recipe.value().getPatterns().stream().anyMatch(stack::is);
    }

    private static int findPatternInventorySlot(Inventory inventory, RecipeHolder<AssemblyRecipe> recipe) {
        for (int slot = 0; slot < Inventory.INVENTORY_SIZE; slot++) {
            if (isValidPattern(inventory.getItem(slot), recipe)) {
                return slot;
            }
        }

        return Inventory.NOT_FOUND_INDEX;
    }

    private static void restoreInventoryItem(Inventory inventory, int slot, ItemStack stack) {
        ItemStack inventoryStack = inventory.getItem(slot);

        if (inventoryStack.isEmpty()) {
            inventory.setItem(slot, stack);
        } else {
            inventoryStack.grow(stack.getCount());
        }
    }

    private boolean canFitPatternChange(
            Inventory inventory,
            ItemStack oldPattern,
            ItemStack newPattern
    ) {
        List<ItemStack> returnedStacks = new ArrayList<>();
        returnedStacks.add(oldPattern);

        CraftingPattern craftingPattern = ((AssemblyPatternItem) newPattern.getItem()).getCraftingPattern();

        for (int slot = 0; slot < this.craftSlots.getContainerSize(); slot++) {
            if (craftingPattern.isSlotDisabled(slot)) {
                returnedStacks.add(this.craftSlots.getItem(slot));
            }
        }

        return canFit(inventory, returnedStacks);
    }

    private static boolean canFit(Inventory inventory, List<ItemStack> returnedStacks) {
        List<ItemStack> simulatedInventory = inventory
                .getNonEquipmentItems()
                .stream()
                .map(ItemStack::copy)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        for (ItemStack returnedStack : returnedStacks) {
            if (!canFit(simulatedInventory, returnedStack)) {
                return false;
            }
        }

        return true;
    }

    private static boolean canFit(List<ItemStack> inventory, ItemStack returnedStack) {
        if (returnedStack.isEmpty()) {
            return true;
        }

        int remaining = returnedStack.getCount();

        for (ItemStack inventoryStack : inventory) {
            if (ItemStack.isSameItemSameComponents(inventoryStack, returnedStack)) {
                int amount = Math.min(
                        remaining,
                        inventoryStack.getMaxStackSize() - inventoryStack.getCount()
                );
                inventoryStack.grow(amount);
                remaining -= amount;
            }

            if (remaining <= 0) {
                return true;
            }
        }

        for (int slot = 0; slot < inventory.size(); slot++) {
            if (!inventory.get(slot).isEmpty()) {
                continue;
            }

            int amount = Math.min(remaining, returnedStack.getMaxStackSize());
            inventory.set(slot, returnedStack.copyWithCount(amount));
            remaining -= amount;

            if (remaining <= 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        this.context.execute((world, pos) -> {
            this.clearContainer(player, this.craftSlots);
            this.clearContainer(player, this.assemblyPatternInventory);
        });
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.context, player, GenesisBlocks.ASSEMBLY_TABLE);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack originalStack;
        Slot selectedSlot = this.slots.get(index);

        if (!selectedSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot = selectedSlot.getItem();
        originalStack = stackInSlot.copy();


        if (index == RESULT_SLOT_INDEX) {
            stackInSlot.getItem().onCraftedBy(stackInSlot, player);

            // Try to insert into player inventory
            if (!this.moveItemStackTo(
                    stackInSlot,
                    INVENTORY_SLOT_START_INDEX,
                    INVENTORY_SLOT_START_INDEX + INVENTORY_SLOT_COUNT,
                    true
            )) {
                return ItemStack.EMPTY;
            }

            selectedSlot.onQuickCraft(stackInSlot, originalStack);
        } else if (
                index >= INVENTORY_SLOT_START_INDEX &&
                        index < INVENTORY_SLOT_START_INDEX + INVENTORY_SLOT_COUNT
        ) {
            // Try to insert into crafting input slots
            if (!this.moveItemStackTo(
                    stackInSlot,
                    CRAFTING_SLOT_START_INDEX,
                    CRAFTING_SLOT_START_INDEX + CRAFTING_SLOT_COUNT,
                    false
            )) {
                // Try the assembly pattern slot
                Slot specialSlot = this.slots.get(PATTERN_SLOT_INDEX);

                boolean movedToPatternSlot = specialSlot.mayPlace(stackInSlot) && this.moveItemStackTo(
                        stackInSlot,
                        PATTERN_SLOT_INDEX,
                        RESULT_SLOT_INDEX,
                        false
                );

                if (!movedToPatternSlot && !this.moveBetweenInventoryAndHotbar(stackInSlot, index)) {
                    return ItemStack.EMPTY;
                }
            }
        } else if (index >= CRAFTING_SLOT_START_INDEX && index < RESULT_SLOT_INDEX) {
            if (!this.moveItemStackTo(
                    stackInSlot,
                    INVENTORY_SLOT_START_INDEX,
                    INVENTORY_SLOT_START_INDEX + INVENTORY_SLOT_COUNT,
                    false
            )) {
                return ItemStack.EMPTY;
            }
        }

        if (stackInSlot.isEmpty()) {
            selectedSlot.setByPlayer(ItemStack.EMPTY);
        } else {
            selectedSlot.setChanged();
        }

        if (stackInSlot.getCount() == originalStack.getCount()) {
            return ItemStack.EMPTY;
        }

        selectedSlot.onTake(player, stackInSlot);

        // Drop leftover result if not inserted
        if (index == RESULT_SLOT_INDEX) {
            player.drop(stackInSlot, false);
        }

        return originalStack;
    }

    private boolean moveBetweenInventoryAndHotbar(ItemStack stack, int sourceSlotIndex) {
        if (sourceSlotIndex < HOTBAR_SLOT_START_INDEX) {
            return this.moveItemStackTo(
                    stack,
                    HOTBAR_SLOT_START_INDEX,
                    INVENTORY_SLOT_START_INDEX + INVENTORY_SLOT_COUNT,
                    false
            );
        }

        return this.moveItemStackTo(
                stack,
                INVENTORY_SLOT_START_INDEX,
                HOTBAR_SLOT_START_INDEX,
                false
        );
    }
}

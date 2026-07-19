package dev.mariany.genesis.screen;

import dev.mariany.genesis.block.GenesisBlocks;
import dev.mariany.genesis.item.custom.AssemblyPatternItem;
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

    private final ContainerLevelAccess context;
    private final Player player;
    private final SimpleContainer assemblyPatternInventory = new SimpleContainer(1);
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

            this.resultSlots.setItem(0, result);
            this.setRemoteSlot(10, result);

            serverPlayer.connection.send(
                    new ClientboundContainerSetSlotPacket(this.containerId, this.incrementStateId(), 10, result)
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
                0,
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
                0,
                x,
                y
        ) {
            @Override
            public void onTake(Player player, ItemStack stack) {
                super.onTake(player, stack);

                AssemblyScreenHandler.this.assemblyPatternInventory.getItem(0).shrink(1);
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
        return this.slots.subList(0, 9);
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


        if (index == 10) { // If the clicked slot is the result slot
            stackInSlot.getItem().onCraftedBy(stackInSlot, player);

            // Try to insert into player inventory
            if (!this.moveItemStackTo(stackInSlot, 11, 47, true)) {
                return ItemStack.EMPTY;
            }

            selectedSlot.onQuickCraft(stackInSlot, originalStack);
        } else if (index >= 11 && index < 47) { // If the clicked slot is in the player inventory
            // Try to insert into input slots (0–8)
            if (!this.moveItemStackTo(stackInSlot, 0, 9, false)) {
                // Try special slot at index 9
                Slot specialSlot = this.slots.get(9);
                if (specialSlot.mayPlace(stackInSlot)) {
                    if (!this.moveItemStackTo(stackInSlot, 9, 10, false)) {
                        return ItemStack.EMPTY;
                    }
                }

                return ItemStack.EMPTY;
            }
        } else if (index >= 0 && index <= 9) { // If the clicked slot is one of the input or special slots
            if (!this.moveItemStackTo(stackInSlot, 11, 47, false)) {
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
        if (index == 10) {
            player.drop(stackInSlot, false);
        }

        return originalStack;
    }
}

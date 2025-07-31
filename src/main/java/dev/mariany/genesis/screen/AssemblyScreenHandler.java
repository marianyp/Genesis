package dev.mariany.genesis.screen;

import dev.mariany.genesis.block.GenesisBlocks;
import dev.mariany.genesis.item.custom.AssemblyPatternItem;
import dev.mariany.genesis.recipe.CraftingPattern;
import dev.mariany.genesis.recipe.GenesisRecipeTypes;
import dev.mariany.genesis.screen.slot.AssemblyInputSlot;
import dev.mariany.genesis.screen.slot.AssemblyPatternSlot;
import dev.mariany.genesis.sound.GenesisSoundEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.book.RecipeBookType;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.AbstractCraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class AssemblyScreenHandler extends AbstractCraftingScreenHandler {
    private final static int SIZE = 3;

    private final ScreenHandlerContext context;
    private final PlayerEntity player;
    private final SimpleInventory assemblyPatternInventory = new SimpleInventory(1);
    private final AssemblyPatternSlot assemblyPatternSlot;
    private final CraftingResultSlot resultSlot;
    private boolean filling;

    public AssemblyScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public AssemblyScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(GenesisScreenHandlers.ASSEMBLY, syncId, SIZE, SIZE);

        this.context = context;
        this.player = playerInventory.player;

        onAssemblyPatternChange(inventory -> {
            this.dropLockedInputs();
            this.onContentChanged(inventory);
            playerInventory.markDirty();
        });

        this.addInputSlots(53, 17);

        this.assemblyPatternSlot = this.addAssemblyPatternSlot();
        this.resultSlot = this.addResultSlot(this.player, 147, 35);

        this.addPlayerSlots(playerInventory, 8, 84);
    }

    public void onAssemblyPatternChange(InventoryChangedListener listener) {
        this.assemblyPatternInventory.addListener(listener);
    }

    private static void offerOrDropStack(PlayerEntity player, ItemStack stack) {
        boolean bl = player.isRemoved() && player.getRemovalReason() != Entity.RemovalReason.CHANGED_DIMENSION;
        boolean bl2 = player instanceof ServerPlayerEntity serverPlayerEntity && serverPlayerEntity.isDisconnected();

        if (bl || bl2) {
            player.dropItem(stack, false);
        } else if (player instanceof ServerPlayerEntity) {
            player.getInventory().offerOrDrop(stack);
        }
    }

    protected void updateResult(ServerWorld world, @Nullable RecipeEntry<CraftingRecipe> recipe) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            CraftingRecipeInput craftingRecipeInput = craftingInventory.createRecipeInput();
            ItemStack result = ItemStack.EMPTY;
            Optional<RecipeEntry<CraftingRecipe>> optionalRecipeEntry = world
                    .getServer()
                    .getRecipeManager()
                    .getFirstMatch(
                            GenesisRecipeTypes.ASSEMBLY,
                            craftingRecipeInput,
                            world,
                            recipe
                    );

            if (optionalRecipeEntry.isPresent()) {
                RecipeEntry<CraftingRecipe> recipeEntry = optionalRecipeEntry.get();
                CraftingRecipe craftingRecipe = recipeEntry.value();

                if (this.craftingResultInventory.shouldCraftRecipe(serverPlayer, recipeEntry)) {
                    ItemStack stack = craftingRecipe.craft(craftingRecipeInput, world.getRegistryManager());
                    boolean stackEnabled = stack.isItemEnabled(world.getEnabledFeatures());
                    boolean assemblyCanCraft = this.getCrafts().map(stack::isIn).orElse(false);

                    if (stackEnabled && assemblyCanCraft) {
                        result = stack;
                    }
                }
            }

            this.craftingResultInventory.setStack(0, result);
            this.setReceivedStack(10, result);

            serverPlayer.networkHandler.sendPacket(
                    new ScreenHandlerSlotUpdateS2CPacket(this.syncId, this.nextRevision(), 10, result)
            );
        }
    }

    private void dropLockedInputs() {
        for (int i = 0; i < this.craftingInventory.size(); i++) {
            if (this.isInputSlotDisabled(i)) {
                offerOrDropStack(player, this.craftingInventory.removeStack(i));
            }
        }

        this.getInputSlots().forEach(Slot::markDirty);
    }

    @Override
    protected void addInputSlots(int x, int y) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                this.addSlot(
                        new AssemblyInputSlot(
                                this,
                                this.craftingInventory,
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
                this.assemblyPatternInventory,
                0,
                28,
                35
        );

        this.addSlot(assemblyPatternSlot);

        return assemblyPatternSlot;
    }

    @Override
    protected CraftingResultSlot addResultSlot(PlayerEntity player, int x, int y) {
        CraftingResultSlot craftingResultSlot = new CraftingResultSlot(player,
                this.craftingInventory,
                this.craftingResultInventory,
                0,
                x,
                y) {
            @Override
            protected void onCrafted(ItemStack stack) {
                super.onCrafted(stack);
                SimpleInventory assemblyPatternInventory = AssemblyScreenHandler.this.assemblyPatternInventory;

                assemblyPatternInventory.getStack(0).decrement(1);
                assemblyPatternInventory.markDirty();
            }

            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                super.onTakeItem(player, stack);

                if (player instanceof ServerPlayerEntity serverPlayer) {
                    AssemblyScreenHandler.this.context.run(
                            (world, pos) ->
                                    serverPlayer.networkHandler.sendPacket(new PlaySoundFromEntityS2CPacket(
                                                    GenesisSoundEvents.BLOCK_ASSEMBLY_TABLE_USE,
                                                    SoundCategory.BLOCKS,
                                                    serverPlayer,
                                                    1,
                                                    1,
                                                    world.getRandom().nextLong()
                                            )
                                    )
                    );
                }
            }
        };

        this.addSlot(craftingResultSlot);

        return craftingResultSlot;
    }

    public Optional<AssemblyPatternItem> getAssemblyPatternItem() {
        if (this.assemblyPatternSlot.getStack().getItem() instanceof AssemblyPatternItem assemblyPatternItem) {
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
    public Slot getOutputSlot() {
        return this.resultSlot;
    }

    @Override
    public List<Slot> getInputSlots() {
        return this.slots.subList(0, 9);
    }

    @Override
    protected PlayerEntity getPlayer() {
        return this.player;
    }

    @Override
    public RecipeBookType getCategory() {
        return RecipeBookType.CRAFTING;
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        if (!this.filling) {
            this.context.run((world, pos) -> {
                if (world instanceof ServerWorld serverWorld) {
                    updateResult(serverWorld, null);
                }
            });
        }
    }

    @Override
    public void onInputSlotFillStart() {
        this.filling = true;
    }

    @Override
    public void onInputSlotFillFinish(ServerWorld world, RecipeEntry<CraftingRecipe> recipe) {
        this.filling = false;
        updateResult(world, recipe);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.context.run((world, pos) -> {
            this.dropInventory(player, this.craftingInventory);
            this.dropInventory(player, this.assemblyPatternInventory);
        });
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, GenesisBlocks.ASSEMBLY_TABLE);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack originalStack;
        Slot selectedSlot = this.slots.get(index);

        if (!selectedSlot.hasStack()) {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot = selectedSlot.getStack();
        originalStack = stackInSlot.copy();


        if (index == 10) { // If the clicked slot is the result slot
            stackInSlot.getItem().onCraftByPlayer(stackInSlot, player);

            // Try to insert into player inventory
            if (!this.insertItem(stackInSlot, 11, 47, true)) {
                return ItemStack.EMPTY;
            }

            selectedSlot.onQuickTransfer(stackInSlot, originalStack);
        } else if (index >= 11 && index < 47) { // If the clicked slot is in the player inventory
            // Try to insert into input slots (0–8)
            if (!this.insertItem(stackInSlot, 0, 9, false)) {
                // Try special slot at index 9
                Slot specialSlot = this.slots.get(9);
                if (specialSlot.canInsert(stackInSlot)) {
                    if (!this.insertItem(stackInSlot, 9, 10, false)) {
                        return ItemStack.EMPTY;
                    }
                }

                return ItemStack.EMPTY;
            }
        } else if (index >= 0 && index <= 9) { // If the clicked slot is one of the input or special slots
            if (!this.insertItem(stackInSlot, 11, 47, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stackInSlot.isEmpty()) {
            selectedSlot.setStack(ItemStack.EMPTY);
        } else {
            selectedSlot.markDirty();
        }

        if (stackInSlot.getCount() == originalStack.getCount()) {
            return ItemStack.EMPTY;
        }

        selectedSlot.onTakeItem(player, stackInSlot);

        // Drop leftover result if not inserted
        if (index == 10) {
            player.dropItem(stackInSlot, false);
        }

        return originalStack;
    }
}

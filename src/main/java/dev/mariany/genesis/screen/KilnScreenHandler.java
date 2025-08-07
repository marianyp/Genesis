package dev.mariany.genesis.screen;

import dev.mariany.genesis.screen.slot.KilnOutputSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.RecipeBookType;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class KilnScreenHandler extends AbstractRecipeScreenHandler {
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    protected final World world;
    private final RecipePropertySet recipePropertySet;

    public KilnScreenHandler(
            int syncId, PlayerInventory playerInventory
    ) {
        this(syncId, playerInventory, new SimpleInventory(2), new ArrayPropertyDelegate(3));
    }

    public KilnScreenHandler(
            int syncId,
            PlayerInventory playerInventory,
            Inventory inventory,
            PropertyDelegate propertyDelegate
    ) {
        super(GenesisScreenHandlers.KILN, syncId);
        checkSize(inventory, 2);
        checkDataCount(propertyDelegate, 3);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.world = playerInventory.player.getWorld();
        this.recipePropertySet = this.world.getRecipeManager().getPropertySet(RecipePropertySet.FURNACE_INPUT);
        this.addSlot(new Slot(inventory, 0, 56, 34));
        this.addSlot(new KilnOutputSlot(playerInventory.player, inventory, 1, 116, 35));
        this.addPlayerSlots(playerInventory, 8, 84);
        this.addProperties(propertyDelegate);
    }

    public Slot getOutputSlot() {
        return this.slots.get(1);
    }

    protected boolean isSmeltable(ItemStack itemStack) {
        return this.recipePropertySet.canUse(itemStack);
    }

    public float getCookProgress() {
        int timeSpent = this.propertyDelegate.get(0);
        int totalTime = this.propertyDelegate.get(1);
        return totalTime != 0 && timeSpent != 0 ? MathHelper.clamp((float) timeSpent / totalTime, 0F, 1F) : 0F;
    }

    public boolean isBurning() {
        return this.propertyDelegate.get(2) == 1;
    }

    @Override
    public RecipeBookType getCategory() {
        return RecipeBookType.FURNACE;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public void populateRecipeFinder(RecipeFinder finder) {
        if (this.inventory instanceof RecipeInputProvider recipeInputProvider) {
            recipeInputProvider.provideRecipeInputs(finder);
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack resultStack = ItemStack.EMPTY;
        Slot clickedSlot = this.slots.get(index);

        if (clickedSlot.hasStack()) {
            ItemStack clickedStack = clickedSlot.getStack();
            resultStack = clickedStack.copy();

            if (index == OUTPUT_SLOT) {
                // Output slot to player inventory
                if (!this.insertItem(clickedStack, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }

                clickedSlot.onQuickTransfer(clickedStack, resultStack);
            } else if (index != INPUT_SLOT) {
                // From player inventory or hotbar
                if (this.isSmeltable(clickedStack)) {
                    if (!this.insertItem(clickedStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 2 && index < 29) {
                    // Main inventory to Hotbar
                    if (!this.insertItem(clickedStack, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 29 && index < 38) {
                    // Hotbar to Main Inventory
                    if (!this.insertItem(clickedStack, 2, 29, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.insertItem(clickedStack, 2, 38, false)) {
                return ItemStack.EMPTY;
            }

            if (clickedStack.isEmpty()) {
                clickedSlot.setStack(ItemStack.EMPTY);
            } else {
                clickedSlot.markDirty();
            }

            if (clickedStack.getCount() == resultStack.getCount()) {
                return ItemStack.EMPTY;
            }

            clickedSlot.onTakeItem(player, clickedStack);
        }

        return resultStack;
    }

    @Override
    public AbstractRecipeScreenHandler.PostFillAction fillInputSlots(
            boolean craftAll, boolean creative, RecipeEntry<?> recipe, ServerWorld world, PlayerInventory inventory
    ) {
        final List<Slot> list = List.of(this.getSlot(0), this.getSlot(2));
        return InputSlotFiller.fill(new InputSlotFiller.Handler<>() {
            @Override
            public void populateRecipeFinder(RecipeFinder finder) {
                KilnScreenHandler.this.populateRecipeFinder(finder);
            }

            @Override
            public void clear() {
                list.forEach(slot -> slot.setStackNoCallbacks(ItemStack.EMPTY));
            }

            @Override
            public boolean matches(RecipeEntry<AbstractCookingRecipe> entry) {
                return entry.value().matches(new SingleStackRecipeInput(KilnScreenHandler.this.inventory.getStack(0)), world);
            }
        }, 1, 1, List.of(this.getSlot(0)), list, inventory, (RecipeEntry<AbstractCookingRecipe>) recipe, craftAll, creative);
    }
}

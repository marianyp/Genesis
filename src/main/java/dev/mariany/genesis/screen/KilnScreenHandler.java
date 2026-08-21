package dev.mariany.genesis.screen;

import dev.mariany.genesis.screen.slot.KilnOutputSlot;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.List;

public class KilnScreenHandler extends RecipeBookMenu {
    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    final Container inventory;
    private final ContainerData propertyDelegate;
    protected final Level level;
    private final RecipePropertySet recipePropertySet;

    public KilnScreenHandler(
            int syncId, Inventory playerInventory
    ) {
        this(syncId, playerInventory, new SimpleContainer(2), new SimpleContainerData(3));
    }

    public KilnScreenHandler(
            int syncId,
            Inventory playerInventory,
            Container inventory,
            ContainerData propertyDelegate
    ) {
        super(GenesisScreenHandlers.KILN, syncId);
        checkContainerSize(inventory, 2);
        checkContainerDataCount(propertyDelegate, 3);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.level = playerInventory.player.level();
        this.recipePropertySet = this.level.recipeAccess().propertySet(RecipePropertySet.FURNACE_INPUT);
        this.addSlot(new Slot(inventory, 0, 56, 34));
        this.addSlot(new KilnOutputSlot(playerInventory.player, inventory, 1, 116, 35));
        this.addStandardInventorySlots(playerInventory, 8, 84);
        this.addDataSlots(propertyDelegate);
    }

    public Slot getOutputSlot() {
        return this.slots.get(1);
    }

    protected boolean isSmeltable(ItemStack itemStack) {
        return this.recipePropertySet.test(itemStack);
    }

    public float getCookProgress() {
        int timeSpent = this.propertyDelegate.get(0);
        int totalTime = this.propertyDelegate.get(1);
        return totalTime != 0 && timeSpent != 0 ? Mth.clamp((float) timeSpent / totalTime, 0F, 1F) : 0F;
    }

    public boolean isBurning() {
        return this.propertyDelegate.get(2) == 1;
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return RecipeBookType.FURNACE;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedItemContents finder) {
        if (this.inventory instanceof StackedContentsCompatible recipeInputProvider) {
            recipeInputProvider.fillStackedContents(finder);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot clickedSlot = this.slots.get(index);

        if (!clickedSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack clickedStack = clickedSlot.getItem();
        ItemStack resultStack = clickedStack.copy();

        if (index == OUTPUT_SLOT) {
            // Output slot to player inventory
            if (!this.moveItemStackTo(clickedStack, 2, 38, true)) {
                return ItemStack.EMPTY;
            }

            clickedSlot.onQuickCraft(clickedStack, resultStack);
        } else if (index != INPUT_SLOT) {
            // From player inventory or hotbar
            if (this.isSmeltable(clickedStack)) {
                if (!this.moveItemStackTo(clickedStack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 2 && index < 29) {
                // Main inventory to Hotbar
                if (!this.moveItemStackTo(clickedStack, 29, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 29 && index < 38) {
                // Hotbar to Main Inventory
                if (!this.moveItemStackTo(clickedStack, 2, 29, false)) {
                    return ItemStack.EMPTY;
                }
            }
        } else if (!this.moveItemStackTo(clickedStack, 2, 38, false)) {
            return ItemStack.EMPTY;
        }

        if (clickedStack.isEmpty()) {
            clickedSlot.setByPlayer(ItemStack.EMPTY);
        } else {
            clickedSlot.setChanged();
        }

        if (clickedStack.getCount() == resultStack.getCount()) {
            return ItemStack.EMPTY;
        }

        clickedSlot.onTake(player, clickedStack);

        return resultStack;
    }

    @Override
    public RecipeBookMenu.PostPlaceAction handlePlacement(
            boolean craftAll,
            boolean creative,
            RecipeHolder<?> recipe,
            ServerLevel level,
            Inventory inventory
    ) {
        final List<Slot> list = List.of(this.getSlot(0), this.getSlot(2));

        return ServerPlaceRecipe.placeRecipe(
                new ServerPlaceRecipe.CraftingMenuAccess<>() {
                    @Override
                    public void fillCraftSlotsStackedContents(StackedItemContents finder) {
                        KilnScreenHandler.this.fillCraftSlotsStackedContents(finder);
                    }

                    @Override
                    public void clearCraftingContent() {
                        list.forEach(slot -> slot.set(ItemStack.EMPTY));
                    }

                    @Override
                    public boolean recipeMatches(RecipeHolder<AbstractCookingRecipe> entry) {
                        SingleRecipeInput input = new SingleRecipeInput(
                                KilnScreenHandler.this.inventory.getItem(0)
                        );

                        return entry.value().matches(input, level);
                    }
                },
                1,
                1,
                List.of(this.getSlot(0)),
                list,
                inventory,
                (RecipeHolder<AbstractCookingRecipe>) recipe,
                craftAll,
                creative
        );
    }
}

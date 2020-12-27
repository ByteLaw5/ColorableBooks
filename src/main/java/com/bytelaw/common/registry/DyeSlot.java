package com.bytelaw.common.registry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Tags;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class DyeSlot extends SlotItemHandler {
    private boolean listenForSlot = true;
    private final ColoringTableContainer container;

    public DyeSlot(ColoringTableContainer container, IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.container = container;
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        if(getHasStack() && listenForSlot) {
            container.addColor();
            listenForSlot = false;
            ItemStack copy = getStack().copy();
            copy.shrink(1);
            putStack(copy);
            listenForSlot = true;
        }
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return Tags.Items.DYES.contains(stack.getItem());
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }
}

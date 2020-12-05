package com.bytelaw.common.integration.category;

import net.minecraft.item.ItemStack;

import java.util.List;

public class ColoringTableRecipe {
    private final List<ItemStack> inputs;
    private final ItemStack output;

    public ColoringTableRecipe(List<ItemStack> inputs, ItemStack output) {
        this.inputs = inputs;
        this.output = output;
    }

    public ItemStack getOutput() {
        return output;
    }

    public List<ItemStack> getInputs() {
        return inputs;
    }
}

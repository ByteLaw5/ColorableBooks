package com.bytelaw.common.integration.category;

import net.minecraft.item.ItemStack;

import java.util.List;

public class ColoringTableRecipe {
    private final ItemStack input;
    private final ItemStack output;
    private final int requiredColor;

    public ColoringTableRecipe(ItemStack input, ItemStack output, int requiredColor) {
        this.input = input;
        this.output = output;
        this.requiredColor = requiredColor;
    }

    public ItemStack getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }

    public int getRequiredColor() {
        return requiredColor;
    }
}

package com.bytelaw.common.integration.category;

import com.bytelaw.ColorableBooks;
import com.bytelaw.common.registry.RegistryList;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.stream.Collectors;

public class ColoringTableCategory implements IRecipeCategory<ColoringTableRecipe> {
    public static final ResourceLocation COLORING_TABLE = ColorableBooks.location("coloring_table_category");
    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;

    public ColoringTableCategory(IGuiHelper helpers) {
        this.background = helpers.createDrawable(ColorableBooks.location("textures/jei_textures.png"), 0, 0, 128, 32);
        this.icon = helpers.createDrawableIngredient(new ItemStack(RegistryList.coloring_table));
        this.localizedName = I18n.format("colorablebooks.coloring_table.name");
    }

    @Override
    public ResourceLocation getUid() {
        return COLORING_TABLE;
    }

    @Override
    public Class<? extends ColoringTableRecipe> getRecipeClass() {
        return ColoringTableRecipe.class;
    }

    @Override
    public String getTitle() {
        return localizedName;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setIngredients(ColoringTableRecipe coloringTableRecipe, IIngredients iIngredients) {
        iIngredients.setInputIngredients(coloringTableRecipe.getInputs().stream().map(ItemStack::getItem).map(Ingredient::fromItems).collect(Collectors.toList()));
        iIngredients.setOutput(VanillaTypes.ITEM, coloringTableRecipe.getOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, ColoringTableRecipe coloringTableRecipe, IIngredients iIngredients) {
        IGuiItemStackGroup stacks = iRecipeLayout.getItemStacks();
        stacks.init(0, true, 2, 7);
        stacks.init(1, true, 51, 7);
        stacks.init(2, false, 109, 7);
        stacks.set(iIngredients);
        stacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
            if(!input)
                tooltip.add(new TranslationTextComponent("colorablebooks.jei.pagetransfer").mergeStyle(TextFormatting.GOLD, TextFormatting.BOLD));
        });
    }
}

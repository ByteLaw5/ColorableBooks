package com.bytelaw.common.integration.category;

import com.bytelaw.ColorableBooks;
import com.bytelaw.common.registry.RegistryList;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.matrix.MatrixStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class ColoringTableCategory implements IRecipeCategory<ColoringTableRecipe> {
    public static final ResourceLocation COLORING_TABLE = ColorableBooks.location("coloring_table_category");
    private final IDrawable background;
    private final IDrawable icon;
    private final String localizedName;
    private final LoadingCache<Integer, IDrawable> cachedColors;

    public ColoringTableCategory(IGuiHelper helpers) {
        ResourceLocation loc = ColorableBooks.location("textures/jei_textures.png");
        this.background = helpers.createDrawable(loc, 0, 0, 128, 32);
        this.icon = helpers.createDrawableIngredient(new ItemStack(RegistryList.coloring_table.get()));
        this.localizedName = I18n.format("colorablebooks.coloring_table.name");
        this.cachedColors = CacheBuilder.newBuilder().maximumSize(2L).build(new CacheLoader<Integer, IDrawable>() {
            @Override
            public IDrawable load(Integer key) {
                return helpers.createDrawable(loc, 128, 0, key * 68 / 100, 3);
            }
        });
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
        iIngredients.setInput(VanillaTypes.ITEM, coloringTableRecipe.getInput());
        iIngredients.setOutput(VanillaTypes.ITEM, coloringTableRecipe.getOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout iRecipeLayout, ColoringTableRecipe coloringTableRecipe, IIngredients iIngredients) {
        IGuiItemStackGroup stacks = iRecipeLayout.getItemStacks();
        stacks.init(0, true, 2, 9);
        stacks.init(2, false, 109, 9);
        stacks.set(iIngredients);
        stacks.addTooltipCallback((slotIndex, input, ingredient, tooltip) -> {
            if(!input)
                tooltip.add(new TranslationTextComponent("colorablebooks.jei.pagetransfer").mergeStyle(TextFormatting.GOLD));
        });
    }

    @Override
    public void draw(ColoringTableRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        IDrawable color = cachedColors.getUnchecked(recipe.getRequiredColor());
        color.draw(matrixStack, 2, 2);
        FontRenderer font = Minecraft.getInstance().fontRenderer;
        font.drawString(matrixStack, recipe.getRequiredColor() + " / 100", 76, 0, 16777215);
    }
}

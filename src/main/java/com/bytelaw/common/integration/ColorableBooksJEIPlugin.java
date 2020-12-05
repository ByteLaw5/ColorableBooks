package com.bytelaw.common.integration;

import com.bytelaw.ColorableBooks;
import com.bytelaw.common.integration.category.ColoringTableCategory;
import com.bytelaw.common.integration.category.ColoringTableRecipe;
import com.bytelaw.common.registry.ColoringTableContainer;
import com.bytelaw.common.registry.RegistryList;
import com.google.common.collect.Lists;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.api.registration.*;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Constants;

import java.util.List;

@JeiPlugin
public class ColorableBooksJEIPlugin implements IModPlugin {
    public ColoringTableCategory coloringTable;

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(Items.WRITTEN_BOOK, stack -> stack.hasTag() ? (stack.getTag().contains("colorable", Constants.NBT.TAG_BYTE) ? "writtenColorable" : "written") : "written");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        if(coloringTable == null)
            coloringTable = new ColoringTableCategory(guiHelper);
        registration.addRecipeCategories(coloringTable);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<ColoringTableRecipe> recipes = Lists.newArrayList();
        for(Item item : Tags.Items.DYES.getAllElements()) {
            for(int i = 0; i < 2; i++) {
                List<ItemStack> inputs = Lists.newArrayList();
                inputs.add(new ItemStack(i == 0 ? Items.WRITABLE_BOOK : Items.WRITTEN_BOOK));
                inputs.add(new ItemStack(item));
                ItemStack output = new ItemStack(RegistryList.colorable_book);
                ListNBT lore = new ListNBT();
                lore.add(StringNBT.valueOf("Copies pages from input!"));
                output.setTagInfo("Lore", lore);
                recipes.add(new ColoringTableRecipe(inputs, output));
            }
        }
        registration.addRecipes(recipes, coloringTable.getUid());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(RegistryList.coloring_table), ColoringTableCategory.COLORING_TABLE);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(new IRecipeTransferInfo<ColoringTableContainer>() {
            @Override
            public Class<ColoringTableContainer> getContainerClass() {
                return ColoringTableContainer.class;
            }

            @Override
            public ResourceLocation getRecipeCategoryUid() {
                return ColoringTableCategory.COLORING_TABLE;
            }

            @Override
            public boolean canHandle(ColoringTableContainer container) {
                return container.getSlot(1).getHasStack() && container.getSlot(1).getStack().getCount() < 64;
            }

            @Override
            public List<Slot> getRecipeSlots(ColoringTableContainer container) {
                List<Slot> slots = Lists.newArrayList();
                slots.add(container.getSlot(0));
                slots.add(container.getSlot(1));
                return slots;
            }

            @Override
            public List<Slot> getInventorySlots(ColoringTableContainer container) {
                List<Slot> slots = Lists.newArrayList();
                for(int i = 3; i < 46; i++)
                    slots.add(container.getSlot(i));
                return slots;
            }
        });
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ColorableBooks.location("jei_plugin");
    }
}

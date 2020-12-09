package com.bytelaw.common.integration;

import com.bytelaw.ColorableBooks;
import com.bytelaw.client.ColoringTableScreen;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import java.util.List;

@JeiPlugin
public class ColorableBooksJEIPlugin implements IModPlugin {
    public ColoringTableCategory coloringTable;

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(Items.WRITTEN_BOOK, stack -> stack.hasTag() ? (stack.getTag().contains("colorable", Constants.NBT.TAG_BYTE) ? "writtenColorable" : "") : "");
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
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 2; j++) {
                recipes.add(new ColoringTableRecipe(new ItemStack(i == 0 ? Items.WRITABLE_BOOK : Items.WRITTEN_BOOK), new ItemStack(j == 0 ? RegistryList.colorable_book : Items.WRITABLE_BOOK), j == 0 ? 10 : 0));
            }
        }
        recipes.stream().mapToInt(ColoringTableRecipe::getRequiredColor).forEachOrdered(System.out::println);
        registration.addRecipes(recipes, coloringTable.getUid());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(RegistryList.coloring_table), ColoringTableCategory.COLORING_TABLE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(ColoringTableScreen.class, 76, 48, 22, 15, ColoringTableCategory.COLORING_TABLE);
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
                return (container.getSlot(0).getHasStack() && container.getSlot(0).getStack().getCount() <= container.getSlot(0).getSlotStackLimit()) || !container.getSlot(0).getHasStack();
            }

            @Override
            public List<Slot> getRecipeSlots(ColoringTableContainer container) {
                List<Slot> slots = Lists.newArrayList();
                slots.add(container.getSlot(0));
                return slots;
            }

            @Override
            public List<Slot> getInventorySlots(ColoringTableContainer container) {
                List<Slot> slots = Lists.newArrayList();
                for(int i = 3; i < container.inventorySlots.size(); i++)
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

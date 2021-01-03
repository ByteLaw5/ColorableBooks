package com.bytelaw.datagen;

import com.bytelaw.common.registry.RegistryList;
import net.minecraft.data.*;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;
import java.util.function.Function;

public class Recipes extends RecipeProvider {
    public static final Function<DataGenerator, Recipes> INSTANCE = Recipes::new;

    private Recipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        ShapelessRecipeBuilder.shapelessRecipe(RegistryList.colorable_book.get())
                .addIngredient(Items.WRITABLE_BOOK)
                .addIngredient(Tags.Items.DYES)
                .addIngredient(Tags.Items.DYES)
                .addIngredient(Tags.Items.DYES)
                .addCriterion("hasItem", hasItem(Items.WRITABLE_BOOK))
                .build(consumer);
        ShapedRecipeBuilder.shapedRecipe(RegistryList.coloring_table_item.get())
                .patternLine("!$$")
                .patternLine("###")
                .patternLine("@ @")
                .key('!', Items.FEATHER)
                .key('$', Tags.Items.DYES)
                .key('#', ItemTags.WOODEN_SLABS)
                .key('@', ItemTags.WOODEN_FENCES)
                .addCriterion("hasItem", hasItem(Items.FEATHER))
                .build(consumer);
    }
}

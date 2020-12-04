package com.bytelaw.common;

import com.bytelaw.client.EditColorableBookScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.WritableBookItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ColorableBook extends WritableBookItem {
    public ColorableBook(Properties builder) {
        super(builder);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        return ActionResultType.PASS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if(worldIn.isRemote) {
            if(stack.getItem() == RegistryList.colorable_book)
                Minecraft.getInstance().displayGuiScreen(new EditColorableBookScreen(playerIn, stack, handIn));
        }
        return ActionResult.resultSuccess(stack);
    }
}

package com.bytelaw.common;

import com.bytelaw.common.network.NetworkManager;
import com.bytelaw.common.network.OpenBookMessage;
import com.bytelaw.common.registry.RegistryList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.WritableBookItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

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
        if(!worldIn.isRemote) {
            if(stack.getItem() == RegistryList.colorable_book)
                NetworkManager.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)playerIn), new OpenBookMessage(stack, handIn));
        }
        return ActionResult.resultSuccess(stack);
    }
}

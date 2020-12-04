package com.bytelaw.client;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class ClientHandlers {
    public static void openColorableBookScreen(ItemStack stack, Hand hand) {
        Minecraft.getInstance().displayGuiScreen(new EditColorableBookScreen(Minecraft.getInstance().player, stack, hand));
    }
}

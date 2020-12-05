package com.bytelaw.client;

import com.bytelaw.common.registry.RegistryList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;

public final class ClientHandlers {
    private ClientHandlers() throws IllegalAccessException {
        throw new IllegalAccessException("How dare you instantiate this class?");
    }

    public static void openColorableBookScreen(ItemStack stack, Hand hand) {
        Minecraft.getInstance().displayGuiScreen(new EditColorableBookScreen(Minecraft.getInstance().player, stack, hand));
    }

    public static void registerScreens() {
        ScreenManager.registerFactory(RegistryList.coloring_table_container, ColoringTableScreen::new);
    }

    public static String updateFormattingCodesForString(String string, boolean andToSection) {
        char[] chars = new char[string.length()];
        string.getChars(0, string.length(), chars, 0);
        for(int j = 0; j < chars.length; j++) {
            if(chars[j] == (andToSection ? '&' : '\u00a7')) {
                if(j + 1 != chars.length && TextFormatting.fromFormattingCode(chars[j + 1]) != null) {
                    StringBuilder b = new StringBuilder(string);
                    b.setCharAt(j, (andToSection ? '\u00a7' : '&'));
                    string = b.toString();
                }
            }
        }
        return string;
    }
}

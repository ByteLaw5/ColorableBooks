package com.bytelaw.client;

import com.bytelaw.common.network.EditColorableBookMessage;
import com.bytelaw.common.network.NetworkManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.EditBookScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Locale;

@OnlyIn(Dist.CLIENT)
public class EditColorableBookScreen extends EditBookScreen {
    public EditColorableBookScreen(PlayerEntity player, ItemStack bookIn, Hand handIn) {
        super(player, bookIn, handIn);
    }

    @Override
    protected void sendBookToServer(boolean publish) {
        if (this.bookIsModified) {
            this.trimEmptyPages();
            if(publish)
                updateFormattingCodes();
            ListNBT listnbt = new ListNBT();
            this.bookPages.stream().map(StringNBT::valueOf).forEach(listnbt::add);
            if (!this.bookPages.isEmpty()) {
                this.book.setTagInfo("pages", listnbt);
            }

            if (publish) {
                this.book.setTagInfo("author", StringNBT.valueOf(this.editingPlayer.getGameProfile().getName()));
                this.book.setTagInfo("title", StringNBT.valueOf(this.bookTitle.trim()));
            }

            int i = this.hand == Hand.MAIN_HAND ? this.editingPlayer.inventory.currentItem : 40;
            NetworkManager.CHANNEL.sendToServer(new EditColorableBookMessage(this.book, publish, i));
        }
    }

    private void updateFormattingCodes() {
        bookTitle = updateFormattingCodesForString(bookTitle);
        for(int i = 0; i < bookPages.size(); i++) {
            String page = bookPages.get(i);
            page = updateFormattingCodesForString(page);
            bookPages.set(i, page);
        }
        func_238751_C_();
    }

    private String updateFormattingCodesForString(String string) {
        char[] chars = new char[string.length()];
        string.getChars(0, string.length(), chars, 0);
        for(int j = 0; j < chars.length; j++) {
            if(chars[j] == '&') {
                if(j + 1 != chars.length && TextFormatting.fromFormattingCode(chars[j + 1]) != null) {
                    StringBuilder b = new StringBuilder(string);
                    b.setCharAt(j, '\u00a7');
                    string = b.toString();
                }
            }
        }
        return string;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        float x = (float)(width + 235) / 2;
        float y = 14;
        for(TextFormatting t : TextFormatting.values()) {
            String s;
            if(t == TextFormatting.OBFUSCATED)
                s = "&" + t.toString().charAt(1) + ": " + new TranslationTextComponent("colorablebooks.obfuscated").getString();
            else
                s = "&" + t.toString().charAt(1) + ": " + t.toString() + new TranslationTextComponent("colorablebooks." + t.name.toLowerCase(Locale.ROOT)).getString();
            font.drawString(matrixStack, s, x, y, 0xffffff);
            y += 10;
        }
    }
}

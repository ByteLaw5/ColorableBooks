package com.bytelaw.client;

import com.bytelaw.ColorableBooksConfig;
import com.bytelaw.common.ClientHandlers;
import com.bytelaw.common.network.EditColorableBookMessage;
import com.bytelaw.common.network.NetworkManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.EditBookScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Locale;

public class EditColorableBookScreen extends EditBookScreen {
    public EditColorableBookScreen(PlayerEntity player, ItemStack bookIn, Hand handIn) {
        super(player, bookIn, handIn);
    }

    @Override
    protected void init() {
        super.init();
        addButton(new Button((width + 235) / 2, 240, 98, 20, new TranslationTextComponent("colorablebook." + (ColorableBooksConfig.Client.SHOW_COLOR_LIST.get() ? "hide" : "show")), b -> {
            ColorableBooksConfig.Client.SHOW_COLOR_LIST.set(!ColorableBooksConfig.Client.SHOW_COLOR_LIST.get());
            b.setMessage(new TranslationTextComponent("colorablebook." + (ColorableBooksConfig.Client.SHOW_COLOR_LIST.get() ? "hide" : "show")));
        }));
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
        bookTitle = ClientHandlers.updateFormattingCodesForString(bookTitle, true);
        for(int i = 0; i < bookPages.size(); i++) {
            String page = bookPages.get(i);
            page = ClientHandlers.updateFormattingCodesForString(page, true);
            bookPages.set(i, page);
        }
        func_238751_C_();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        if(ColorableBooksConfig.Client.SHOW_COLOR_LIST.get()) {
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
}

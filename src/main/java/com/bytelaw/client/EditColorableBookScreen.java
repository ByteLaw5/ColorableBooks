package com.bytelaw.client;

import com.bytelaw.ColorableBooks;
import com.bytelaw.ColorableBooksConfig;
import com.bytelaw.common.ClientHandlers;
import com.bytelaw.common.network.EditColorableBookMessage;
import com.bytelaw.common.network.NetworkManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.EditBookScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Locale;

@OnlyIn(Dist.CLIENT)
public class EditColorableBookScreen extends EditBookScreen {
    private final WidgetImpl selectColorList;

    public EditColorableBookScreen(PlayerEntity player, ItemStack bookIn, Hand handIn) {
        super(player, bookIn, handIn);
        selectColorList = new WidgetImpl((width - 235) / 2 - 231, 175, 198, 38, StringTextComponent.EMPTY);
        selectColorList.visible = false;
    }

    private ResourceLocation getTextureForFormatting(TextFormatting t) {
        return ColorableBooks.location(String.format("textures/gui/colors/%s.png", t.name.toLowerCase(Locale.ROOT)));
    }

    @Override
    protected void init() {
        super.init();
        addButton(new Button((width + 235) / 2, 240, 98, 20, new TranslationTextComponent("colorablebook." + (ColorableBooksConfig.Client.SHOW_COLOR_LIST.get() ? "hide" : "show")), b -> {
            ColorableBooksConfig.Client.SHOW_COLOR_LIST.set(!ColorableBooksConfig.Client.SHOW_COLOR_LIST.get());
            b.setMessage(new TranslationTextComponent("colorablebook." + (ColorableBooksConfig.Client.SHOW_COLOR_LIST.get() ? "hide" : "show")));
        }));
        addButton(new ImageButton((width - 219) / 2, 120, 16, 16, 0, 0, 0, ColorableBooks.location("textures/gui/toggle_color_list.png"), (b) -> selectColorList.visible = !selectColorList.visible));
        addButton(selectColorList);
        int baseX = ((width - 235) / 2 - 231) + 2;
        int baseY = 175 + 2;
        int index = 0;
        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 11; j++) {
                baseY += (16 + 2);
                baseX += (16 + 2);
                TextFormatting t = TextFormatting.values()[index];
                ImageButton b = new ImageButton(baseX, baseY, 16, 16, 0, 0, 0, getTextureForFormatting(t), (button) -> {
                    if(field_238748_u_.hasSelection()) {
                        field_238748_u_.moveCursorTo(field_238748_u_.getSelectionStart(), true);
                        field_238748_u_.putText(t.toString());
                    }
                });
                selectColorList.addChild(b);
                index++;
            }
        }
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

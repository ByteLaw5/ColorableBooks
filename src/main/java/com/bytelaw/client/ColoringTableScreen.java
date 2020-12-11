package com.bytelaw.client;

import com.bytelaw.ColorableBooks;
import com.bytelaw.common.registry.ColoringTableContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ColoringTableScreen extends ContainerScreen<ColoringTableContainer> {
    private static final ResourceLocation TEXTURES = ColorableBooks.location("textures/coloring_table.png");

    public ColoringTableScreen(ColoringTableContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderHoveredTooltip(matrixStack, mouseX, mouseY);
        if(mouseX >= 18 + guiLeft && mouseX <= 87 + guiLeft)
            if(mouseY >= 37 + guiTop && mouseY <= 41 + guiTop)
                renderTooltip(matrixStack, new StringTextComponent(container.getColor() + " / 100"), mouseX, mouseY);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1, 1, 1, 1);
        minecraft.getTextureManager().bindTexture(TEXTURES);
        int i = guiLeft;
        int j = guiTop;
        blit(matrixStack, i, j, 0, 0, xSize, ySize);
        int l = container.getColor();
        blit(matrixStack, i + 19, j + 38, 176, 0, l * 68 / 100, 3);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        titleX = 50;
        super.drawGuiContainerForegroundLayer(matrixStack, x, y);
    }
}

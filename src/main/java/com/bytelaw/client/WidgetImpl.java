package com.bytelaw.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public class WidgetImpl extends Widget {
    private final List<IRenderable> children = Lists.newArrayList();

    public WidgetImpl(int x, int y, int width, int height, ITextComponent title) {
        super(x, y, width, height, title);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        if(!children.isEmpty())
            children.forEach(r -> r.render(matrixStack, mouseX, mouseY, partialTicks));
    }

    public void addChild(IRenderable i) {
        children.add(i);
    }
}

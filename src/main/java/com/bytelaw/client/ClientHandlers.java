package com.bytelaw.client;

import com.bytelaw.common.registry.RegistryList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.particle.Particle;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

import java.util.Random;

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

    public static void spawnColorParticles(BlockPos pos) {
        Random rand = new Random();
        for(int i = 0; i < 10; i++) {
            float r = rand.nextFloat() / 2f + 0.5f;
            float g = rand.nextFloat() / 2f + 0.5f;
            float b = rand.nextFloat() / 2f + 0.5f;
            Particle particle = Minecraft.getInstance().particles.addParticle(ParticleTypes.EFFECT, pos.getX() + 0.5D, pos.getY() + 0.75D, pos.getZ() + 0.5D, rand.nextGaussian() * 0.25F, rand.nextGaussian() * 0.75F, rand.nextGaussian() * 0.25F);
            particle.setColor(r, g, b);
        }
    }
}

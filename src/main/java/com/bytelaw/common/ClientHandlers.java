package com.bytelaw.common;

import com.bytelaw.client.ColoringTableScreen;
import com.bytelaw.client.EditColorableBookScreen;
import com.bytelaw.common.registry.RegistryList;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.particle.Particle;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.Random;

public final class ClientHandlers {
    private static final Minecraft MINECRAFT = Minecraft.getInstance();

    private static boolean isOnServer() {
        return FMLEnvironment.dist.isDedicatedServer();
    }

    private ClientHandlers() throws IllegalAccessException {
        throw new IllegalAccessException("How dare you instantiate this class?");
    }

    public static void openColorableBookScreen(ItemStack stack, Hand hand) {
        if(isOnServer())
            return;
        MINECRAFT.displayGuiScreen(new EditColorableBookScreen(MINECRAFT.player, stack, hand));
    }

    public static void registerScreens() {
        if(isOnServer())
            return;
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

    public static void spawnColorParticles(BlockPos pos, int multiplier) {
        if(isOnServer())
            return;
        Random rand = new Random();
        for(int i = 0; i < (10 * multiplier); i++) {
            float r = rand.nextFloat() * 2.F / (rand.nextFloat() * 1.25F) + rand.nextInt(155);
            float g = rand.nextFloat() * 2.F / (rand.nextFloat() * 1.25F) + rand.nextInt(155);
            float b = rand.nextFloat() * 2.F / (rand.nextFloat() * 1.25F) + rand.nextInt(155);
            Particle particle = MINECRAFT.particles.addParticle(ParticleTypes.EFFECT, pos.getX() + 0.5D, pos.getY() + 0.75D, pos.getZ() + 0.5D, rand.nextGaussian() * 0.25F, rand.nextGaussian() * 0.75F, rand.nextGaussian() * 0.25F);
            particle.setColor(r, g, b);
            particle.setMaxAge(particle.getMaxAge() * 2);
        }
    }

    public static BlockState getClientBlockstate(BlockPos pos) {
        if(isOnServer())
            return Blocks.AIR.getDefaultState();
        return MINECRAFT.world.getBlockState(pos);
    }
}

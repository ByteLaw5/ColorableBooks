package com.bytelaw.common;

import com.bytelaw.client.ColoringTableScreen;
import com.bytelaw.client.EditColorableBookScreen;
import com.bytelaw.common.registry.RegistryList;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
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

    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ScreenManager.registerFactory(RegistryList.coloring_table_container.get(), ColoringTableScreen::new));
    }

    public static String updateFormattingCodesForString(String string, boolean andToSection) {
        final char[] chars = string.toCharArray();
        StringBuilder result = new StringBuilder();
        for(int j = 0; j < chars.length; j++) {
            result.setCharAt(j, chars[j]);
            if(chars[j] == (andToSection ? '&' : '\u00a7')) {
                if(j + 1 != chars.length && TextFormatting.fromFormattingCode(chars[j + 1]) != null) {
                    result.setCharAt(j, (andToSection ? '\u00a7' : '&'));
                }
            }
        }
        return result.toString();
    }

    public static void spawnColorParticles(BlockPos pos, int multiplier) {
        if(isOnServer())
            return;
        if(MINECRAFT.gameSettings.particles != ParticleStatus.MINIMAL) {
            Random rand = new Random();
            for (int i = 0; i < (10 * multiplier); i++) {
                float r = rand.nextFloat() * 2.F / (rand.nextFloat() * 1.25F) + rand.nextInt(155);
                float g = rand.nextFloat() * 2.F / (rand.nextFloat() * 1.25F) + rand.nextInt(155);
                float b = rand.nextFloat() * 2.F / (rand.nextFloat() * 1.25F) + rand.nextInt(155);
                Particle particle = MINECRAFT.particles.addParticle(ParticleTypes.EFFECT, pos.getX() + 0.5D, pos.getY() + 0.75D, pos.getZ() + 0.5D, rand.nextGaussian() * 0.25F, rand.nextGaussian() * 0.75F, rand.nextGaussian() * 0.25F);
                particle.setColor(r, g, b);
                particle.setMaxAge(particle.getMaxAge() * 2);
            }
        }
    }

    public static void handleExplosion(double motionX, double motionY, double motionZ) {
        MINECRAFT.player.setMotion(MINECRAFT.player.getMotion().add(motionX, motionY, motionZ));
    }

    public static BlockState getClientBlockstate(BlockPos pos) {
        if(isOnServer())
            return Blocks.AIR.getDefaultState();
        return MINECRAFT.world.getBlockState(pos);
    }
}

package com.bytelaw;

import com.bytelaw.common.ClientHandlers;
import com.bytelaw.common.network.NetworkManager;
import com.bytelaw.datagen.LootTables;
import com.bytelaw.datagen.Recipes;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.bytelaw.common.registry.RegistryList.*;

@Mod(ColorableBooks.MODID)
public class ColorableBooks {
    @Nonnull
    public static final String MODID = "colorablebooks";

    public ColorableBooks() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ColorableBooksConfig.CLIENT);
        NetworkManager.registerMessages();
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        BLOCKS.register(bus);
        TILE_ENTITIES.register(bus);
        CONTAINERS.register(bus);
        bus.addListener(this::gatherData);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.addListener(this::clientSetup));
    }

    private void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        gen.addProvider(Recipes.INSTANCE.apply(gen));
        gen.addProvider(LootTables.INSTANCE.apply(gen));
    }

    private void clientSetup(FMLClientSetupEvent event) {
        ClientHandlers.onClientSetup(event);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    public static ResourceLocation location(@Nonnull String name) {
        return new ResourceLocation(MODID, name);
    }
}
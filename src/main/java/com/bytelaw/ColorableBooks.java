package com.bytelaw;

import com.bytelaw.common.ColorableBook;
import com.bytelaw.common.network.NetworkManager;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@Mod(ColorableBooks.MODID)
public class ColorableBooks {
    public static final String MODID = "colorablebooks";

    public ColorableBooks() {
        NetworkManager.registerMessages();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addGenericListener(Item.class, this::registerItem);
    }

    private void registerItem(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new ColorableBook(new Item.Properties().group(ItemGroup.MISC).maxStackSize(1)).setRegistryName(location("colorable_book"))
        );
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    public static ResourceLocation location(@Nonnull String name) {
        return new ResourceLocation(MODID, name);
    }
}
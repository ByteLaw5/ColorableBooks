package com.bytelaw;

import com.bytelaw.client.ClientHandlers;
import com.bytelaw.common.*;
import com.bytelaw.common.network.NetworkManager;
import com.bytelaw.common.registry.ColoringTableBlock;
import com.bytelaw.common.registry.ColoringTableContainer;
import com.bytelaw.common.registry.ColoringTableTile;
import com.bytelaw.common.registry.RegistryList;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@Mod(ColorableBooks.MODID)
public class ColorableBooks {
    @Nonnull
    public static final String MODID = "colorablebooks";

    public ColorableBooks() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ColorableBooksConfig.CLIENT);
        NetworkManager.registerMessages();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addGenericListener(Item.class, this::registerItem);
        bus.addGenericListener(Block.class, this::registerBlock);
        bus.addGenericListener(TileEntityType.class, this::registerTile);
        bus.addGenericListener(ContainerType.class, this::registerContainer);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.addListener(this::clientSetup));
    }

    private void clientSetup(FMLClientSetupEvent event) {
        ClientHandlers.registerScreens();
    }

    private void registerItem(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(
                new ColorableBook(new Item.Properties().group(ItemGroup.MISC).maxStackSize(1)).setRegistryName(location("colorable_book")),
                new BlockItem(RegistryList.coloring_table, new Item.Properties().group(ItemGroup.MISC).maxStackSize(1)).setRegistryName(RegistryList.coloring_table.getRegistryName())
        );
    }

    private void registerBlock(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                new ColoringTableBlock(AbstractBlock.Properties.create(Material.WOOD).setRequiresTool().hardnessAndResistance(2.0F).harvestTool(ToolType.AXE).harvestLevel(1)).setRegistryName(location("coloring_table"))
        );
    }

    private void registerTile(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().registerAll(
                TileEntityType.Builder.create(ColoringTableTile::new, RegistryList.coloring_table).build(null).setRegistryName(location("coloring_table"))
        );
    }

    private void registerContainer(RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().registerAll(
                IForgeContainerType.create((id, inv, data) -> {
                    BlockPos pos = data.readBlockPos();
                    return new ColoringTableContainer(id, inv, pos, IWorldPosCallable.of(inv.player.world, pos));
                }).setRegistryName(location("coloring_table"))
        );
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientHandlers::registerScreens);
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    public static ResourceLocation location(@Nonnull String name) {
        return new ResourceLocation(MODID, name);
    }
}
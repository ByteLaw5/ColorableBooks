package com.bytelaw.common.registry;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.bytelaw.ColorableBooks.MODID;

public class RegistryList {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MODID);
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);

    public static final RegistryObject<ColorableBook> colorable_book = ITEMS.register("colorable_book", () -> new ColorableBook(new Item.Properties().group(ItemGroup.MISC).maxStackSize(1)));
    public static final RegistryObject<ColoringTableBlock> coloring_table = BLOCKS.register("coloring_table", () -> new ColoringTableBlock(AbstractBlock.Properties.create(Material.WOOD).hardnessAndResistance(1.65F).harvestTool(ToolType.AXE).notSolid()));
    public static final RegistryObject<BlockItem> coloring_table_item = ITEMS.register("coloring_table", () -> new BlockItem(coloring_table.get(), new Item.Properties().group(ItemGroup.MISC).maxStackSize(1)));
    public static final RegistryObject<TileEntityType<ColoringTableTile>> coloring_table_te = TILE_ENTITIES.register("coloring_table", () -> TileEntityType.Builder.create(ColoringTableTile::new, coloring_table.get()).build(null));
    public static final RegistryObject<ContainerType<ColoringTableContainer>> coloring_table_container = CONTAINERS.register("coloring_table", () -> IForgeContainerType.create((id, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        World world = inv.player.world;
        return new ColoringTableContainer(id, inv, pos, IWorldPosCallable.of(world, pos));
    }));
}

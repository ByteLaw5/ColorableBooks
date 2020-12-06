package com.bytelaw.common.registry;

import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class RegistryList {
    /*
     * Start items
     */
    @ObjectHolder("colorablebooks:colorable_book")
    public static final ColorableBook colorable_book = null;
    @ObjectHolder("colorablebooks:coloring_table")
    public static final BlockItem coloring_table_item = null;
    /*
     * End items
     */

    /*
     * Start TE's
     */
    @ObjectHolder("colorablebooks:coloring_table")
    public static final TileEntityType<ColoringTableTile> coloring_table_te = null;
    /*
     * End TE's
     */

    /*
     * Start Blocks
     */
    @ObjectHolder("colorablebooks:coloring_table")
    public static final ColoringTableBlock coloring_table = null;
    /*
     * End Blocks
     */

    /*
     * Start Containers
     */
    @ObjectHolder("colorablebooks:coloring_table")
    public static final ContainerType<ColoringTableContainer> coloring_table_container = null;
}

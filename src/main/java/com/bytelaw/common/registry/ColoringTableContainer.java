package com.bytelaw.common.registry;

import com.bytelaw.client.ClientHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class ColoringTableContainer extends Container {
    private final IWorldPosCallable callable;
    private final PlayerInventory playerInventory;
    private final BlockPos pos;

    public ColoringTableContainer(int id, PlayerInventory playerInventory, BlockPos pos, IWorldPosCallable callable) {
        super(RegistryList.coloring_table_container, id);
        this.callable = callable;
        this.playerInventory = playerInventory;
        this.pos = pos;
        items().ifPresent(handler -> {
            addSlot(new SlotItemHandler(handler, 0, 40, 20) {
                @Override
                public void onSlotChanged() {
                    super.onSlotChanged();
                    ColoringTableContainer.this.onSlotChanged();
                }

                @Override
                public boolean isItemValid(@Nonnull ItemStack stack) {
                    return stack.getItem() == Items.WRITABLE_BOOK || stack.getItem() == Items.WRITTEN_BOOK;
                }

                @Override
                public int getSlotStackLimit() {
                    return 1;
                }
            });
            addSlot(new SlotItemHandler(handler, 1, 70, 20) {
                @Override
                public void onSlotChanged() {
                    super.onSlotChanged();
                    ColoringTableContainer.this.onSlotChanged();
                }

                @Override
                public boolean isItemValid(@Nonnull ItemStack stack) {
                    return Tags.Items.DYES.contains(stack.getItem());
                }
            });
            addSlot(new SlotItemHandler(handler, 2, 48, 38) {
                @Override
                public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
                    getSlot(0).putStack(ItemStack.EMPTY);
                    ItemStack s = getSlot(1).getStack();
                    s.shrink(1);
                    getSlot(1).putStack(s);
                    return super.onTake(thePlayer, stack);
                }

                @Override
                public boolean isItemValid(@Nonnull ItemStack stack) {
                    return false;
                }
            });
        });

        int i = -54;
        for(int l = 0; l < 3; ++l) {
            for(int j1 = 0; j1 < 9; ++j1) {
                System.out.println(j1 + l * 9 + 3);
                this.addSlot(new Slot(playerInventory, j1 + l * 9 + 3, 8 + j1 * 18, 103 + l * 18 + i));
            }
        }

        for(int i1 = 0; i1 < 9; ++i1) {
            System.out.println(39 + i1);
            this.addSlot(new Slot(playerInventory, 39 + i1, 8 + i1 * 18, 161 + i));
        }
    }

    private ColoringTableTile getTile() {
        TileEntity te = playerInventory.player.world.getTileEntity(pos);
        if(te instanceof ColoringTableTile) {
            return (ColoringTableTile)te;
        }
        return null; //Almost never unlikely to ever happen
    }

    private LazyOptional<IItemHandler> items() {
        return getTile().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
    }

    private void onSlotChanged() {
        if(getSlot(0).getHasStack() && getSlot(1).getHasStack() && !getSlot(2).getHasStack()) {
            ItemStack result = new ItemStack(RegistryList.colorable_book);
            ItemStack book = getSlot(0).getStack();
            CompoundNBT nbt = book.getTag();
            result.setTagInfo("pages", nbt.getList("pages", Constants.NBT.TAG_STRING));
            if(book.getItem() == Items.WRITTEN_BOOK && nbt.contains("colorable", Constants.NBT.TAG_BYTE)) {
                ListNBT listNBT = nbt.getList("pages", Constants.NBT.TAG_STRING);
                ListNBT newList = new ListNBT();
                for(int i = 0; i < listNBT.size(); i++) {
                    String s = listNBT.getString(i);
                    s = ClientHandlers.updateFormattingCodesForString(s, false);
                    newList.add(i, StringNBT.valueOf(s));
                }
                result.setTagInfo("pages", newList);
            }
            getSlot(2).putStack(result);
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(callable, playerIn, RegistryList.coloring_table);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        return super.transferStackInSlot(playerIn, index);
    }
}

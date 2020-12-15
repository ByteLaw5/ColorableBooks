package com.bytelaw.common.registry;

import com.bytelaw.common.ClientHandlers;
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
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.AbstractList;
import java.util.stream.Collector;

public class ColoringTableContainer extends Container {
    private final IWorldPosCallable callable;
    private final PlayerInventory playerInventory;
    private final BlockPos pos;

    public ColoringTableContainer(int id, PlayerInventory playerInventory, BlockPos pos, IWorldPosCallable callable) {
        super(RegistryList.coloring_table_container, id);
        this.callable = callable;
        this.playerInventory = playerInventory;
        this.pos = pos;
        trackInt(new IntReferenceHolder() {
            @Override
            public int get() {
                return getColor();
            }

            @Override
            public void set(int value) {
                getTile().setColor(value);
            }
        });
        getTile().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            addSlot(new SlotItemHandler(handler, 0, 27, 47) {
                @Override
                public void onSlotChanged() {
                    super.onSlotChanged();
                    ColoringTableContainer.this.onSlotChange();
                }

                @Override
                public boolean isItemValid(@Nonnull ItemStack stack) {
                    return stack.getItem() == Items.WRITABLE_BOOK || stack.getItem() == Items.WRITTEN_BOOK;
                }

                @Override
                public int getSlotStackLimit() {
                    return 1;
                }

                @Override
                public int getItemStackLimit(@Nonnull ItemStack stack) {
                    return getSlotStackLimit();
                }
            });
            addSlot(new SlotItemHandler(handler, 1, 19, 18) {
                @Override
                public void onSlotChanged() {
                    super.onSlotChanged();
                    if(getHasStack()) {
                        addColor();
                        getStack().shrink(1);
                    }
                }

                @Override
                public boolean isItemValid(@Nonnull ItemStack stack) {
                    return Tags.Items.DYES.contains(stack.getItem());
                }

                @Override
                public int getSlotStackLimit() {
                    return 1;
                }
            });
            addSlot(new SlotItemHandler(handler, 2, 134, 47) {
                @Override
                public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
                    getSlot(0).putStack(ItemStack.EMPTY);
                    ItemStack s = getSlot(1).getStack();
                    s.shrink(1);
                    getSlot(1).putStack(s);
                    getTile().spawnColorParticles();
                    getTile().setColor(MathHelper.clamp(getColor() - 10, 0, 100));
                    return super.onTake(thePlayer, stack);
                }

                @Override
                public boolean isItemValid(@Nonnull ItemStack stack) {
                    return false;
                }
            });
        });

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    private ColoringTableTile getTile() {
        TileEntity te = playerInventory.player.world.getTileEntity(pos);
        if(te instanceof ColoringTableTile) {
            return (ColoringTableTile)te;
        }
        return null; //Almost never unlikely to ever happen
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(callable, playerIn, RegistryList.coloring_table);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index == 2) {
                if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (index != 0 && index != 1) {
                if (index >= 3 && index < 39) {
                    int i = Tags.Items.DYES.contains(itemstack.getItem()) ? 1 : 0;
                    if (!this.mergeItemStack(itemstack1, i, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.mergeItemStack(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            }
            slot.onSlotChanged();

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    /*
    Pays attention to slot stack limits.
     */
    @Override
    protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        boolean flag = false;
        int i = startIndex;
        if (reverseDirection) {
            i = endIndex - 1;
        }

        if (stack.isStackable()) {
            while(!stack.isEmpty()) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot = this.inventorySlots.get(i);
                ItemStack itemstack = slot.getStack();
                if (!itemstack.isEmpty() && areItemsAndTagsEqual(stack, itemstack)) {
                    int j = itemstack.getCount() + stack.getCount();
                    int maxSize = Math.min(slot.getItemStackLimit(itemstack), Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize()));
                    if(j > maxSize) {
                        slot.putStack(stack.split(maxSize));
                        flag = true;
                    }
                }

                if (reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        if (!stack.isEmpty()) {
            if (reverseDirection) {
                i = endIndex - 1;
            } else {
                i = startIndex;
            }

            while(true) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot1 = this.inventorySlots.get(i);
                ItemStack itemstack1 = slot1.getStack();
                if (itemstack1.isEmpty() && slot1.isItemValid(stack)) {
                    int limit = Math.min(slot1.getSlotStackLimit(), slot1.getItemStackLimit(stack));
                    if (stack.getCount() > limit) {
                        slot1.putStack(stack.split(limit));
                    } else {
                        slot1.putStack(stack.split(stack.getCount()));
                    }

                    slot1.onSlotChanged();
                    flag = true;
                    break;
                }

                if (reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        return flag;
    }

    private void onSlotChange() {
        if(getSlot(0).getHasStack()) {
            ItemStack result = new ItemStack(getColor() >= 10 ? RegistryList.colorable_book : Items.WRITABLE_BOOK);
            ItemStack book = getSlot(0).getStack();
            if(book.hasTag()) {
                CompoundNBT nbt = book.getTag();
                if(nbt.contains("pages", Constants.NBT.TAG_LIST)) {
                    ListNBT listNBTresult = nbt.getList("pages", Constants.NBT.TAG_STRING).copy();
                    if(book.getItem() == Items.WRITTEN_BOOK)
                        if(nbt.contains("colorable", Constants.NBT.TAG_BYTE))
                            listNBTresult = listNBTresult.stream().map(stringnbt -> ITextComponent.Serializer.getComponentFromJson(stringnbt.getString()).getString()).map(string -> ClientHandlers.updateFormattingCodesForString(string, false))
                                    .map(StringNBT::valueOf).collect(toListNBT());
                        else
                            listNBTresult = listNBTresult.stream().map(stringnbt -> ITextComponent.Serializer.getComponentFromJson(stringnbt.getString()).getString())
                                    .map(StringNBT::valueOf).collect(toListNBT());
                    result.setTagInfo("pages", listNBTresult.copy());
                }
            }
            getSlot(2).putStack(result);
        }
        if((!getSlot(0).getHasStack()) && getSlot(2).getHasStack())
            getSlot(2).putStack(ItemStack.EMPTY);
    }

    private void addColor() {
        int toAdd = 10;
        if(getColor() < 100) {
            getTile().setColor(toAdd + getColor());
            onSlotChange();
        }
    }

    private static Collector<StringNBT, ListNBT, ListNBT> toListNBT() {
        return Collector.of(ListNBT::new, AbstractList::add, (list1, list2) -> {
            list1.addAll(list2);
            return list1;
        });
    }

    public int getColor() {
        return getTile().getColor();
    }
}

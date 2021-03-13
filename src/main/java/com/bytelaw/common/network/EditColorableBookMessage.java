package com.bytelaw.common.network;

import com.bytelaw.ColorableBooks;
import com.bytelaw.common.registry.RegistryList;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WritableBookItem;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid=ColorableBooks.MODID,value=Dist.CLIENT)
public class EditColorableBookMessage {
    public final ItemStack book;
    public final boolean updateAll;
    public final int inventoryIndex;

    public EditColorableBookMessage(ItemStack book, boolean updateAll, int inventoryIndex) {
        this.book = book;
        this.updateAll = updateAll;
        this.inventoryIndex = inventoryIndex;
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeItemStack(book);
        buffer.writeBoolean(updateAll);
        buffer.writeVarInt(inventoryIndex);
    }

    public static EditColorableBookMessage decode(PacketBuffer buffer) {
        ItemStack book = buffer.readItemStack();
        boolean updateAll = buffer.readBoolean();
        int i = buffer.readVarInt();
        return new EditColorableBookMessage(book, updateAll, i);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if(book.getItem() == RegistryList.colorable_book) {
                CompoundNBT compoundnbt = book.getTag();
                if(WritableBookItem.isNBTValid(compoundnbt)) {
                    List<String> list = Lists.newArrayList();
                    String title = null;
                    if(updateAll)
                        title = compoundnbt.getString("title");

                    ListNBT listnbt = compoundnbt.getList("pages", Constants.NBT.TAG_STRING);

                    for(int i = 0; i < listnbt.size(); i++) {
                        list.add(listnbt.getString(i));
                    }

                    if(PlayerInventory.isHotbar(inventoryIndex) || inventoryIndex == 40) {
                        replaceBookWithWritten(ctx.get().getSender(), title, list, inventoryIndex);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private void replaceBookWithWritten(ServerPlayerEntity player, @Nullable String title, List<String> list, int invIndex) {
        ItemStack itemstack = player.inventory.getStackInSlot(invIndex);
        if (itemstack.getItem() == RegistryList.colorable_book) {
            if(updateAll) {
                ItemStack itemstack1 = new ItemStack(Items.WRITTEN_BOOK);
                CompoundNBT compoundnbt = itemstack.getTag();
                if (compoundnbt != null) {
                    itemstack1.setTag(compoundnbt.copy());
                }

                itemstack1.setTagInfo("author", StringNBT.valueOf(player.getName().getString()));
                itemstack1.setTagInfo("title", StringNBT.valueOf(title));
                ListNBT listnbt = new ListNBT();

                for(String s : list) {
                    ITextComponent itextcomponent = new StringTextComponent(s);
                    String s1 = ITextComponent.Serializer.toJson(itextcomponent);
                    listnbt.add(StringNBT.valueOf(s1));
                }

                itemstack1.setTagInfo("pages", listnbt);
                itemstack1.setTagInfo("colorable", ByteNBT.ONE);
                player.inventory.setInventorySlotContents(invIndex, itemstack1);
            } else {
                ListNBT listnbt = new ListNBT();
                list.stream().map(StringNBT::valueOf).forEach(listnbt::add);
                itemstack.setTagInfo("pages", listnbt);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @OnlyIn(Dist.CLIENT)
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if(!stack.hasTag())
            return;
        CompoundNBT nbt = stack.getTag();
        if(!nbt.contains("colorable", Constants.NBT.TAG_BYTE))
            return;
        event.getToolTip().add(new StringTextComponent("Colorable Book").mergeStyle(TextFormatting.GOLD));
    }
}

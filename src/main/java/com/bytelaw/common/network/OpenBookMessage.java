package com.bytelaw.common.network;

import com.bytelaw.common.ClientHandlers;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenBookMessage {
    private final ItemStack stack;
    private final Hand hand;

    public OpenBookMessage(ItemStack stack, Hand hand) {
        this.stack = stack;
        this.hand = hand;
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeItemStack(stack);
        buffer.writeEnumValue(hand);
    }

    public static OpenBookMessage decode(PacketBuffer buffer) {
        ItemStack stack = buffer.readItemStack();
        Hand hand = buffer.readEnumValue(Hand.class);
        return new OpenBookMessage(stack, hand);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientHandlers.openColorableBookScreen(stack, hand));
        ctx.get().setPacketHandled(true);
    }
}

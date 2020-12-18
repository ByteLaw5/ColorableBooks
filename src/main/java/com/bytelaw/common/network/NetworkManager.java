package com.bytelaw.common.network;

import com.bytelaw.ColorableBooks;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkManager {
    public static final String PROTOCOL_VERSION = "1.0";
    public static SimpleChannel CHANNEL;
    private static int id = 0;

    public static void registerMessages() {
        CHANNEL = NetworkRegistry.newSimpleChannel(
                ColorableBooks.location("network_channel"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals);
        CHANNEL.registerMessage(
                id++,
                EditColorableBookMessage.class,
                EditColorableBookMessage::encode,
                EditColorableBookMessage::decode,
                EditColorableBookMessage::handle
        );
        CHANNEL.registerMessage(
                id++,
                OpenBookMessage.class,
                OpenBookMessage::encode,
                OpenBookMessage::decode,
                OpenBookMessage::handle
        );
    }
}

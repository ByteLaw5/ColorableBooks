package com.bytelaw.common.network;

import com.bytelaw.ColorableBooks;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkManager {
    public static SimpleChannel CHANNEL;
    private static int id = 0;

    public static void registerMessages() {
        CHANNEL = NetworkRegistry.newSimpleChannel(ColorableBooks.location("network_channel"), () -> "1.0", s -> true, s -> true);
        CHANNEL.registerMessage(
                id++,
                EditColorableBookMessage.class,
                EditColorableBookMessage::encode,
                EditColorableBookMessage::decode,
                EditColorableBookMessage::handle
        );
    }
}

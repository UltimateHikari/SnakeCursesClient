package me.hikari.snakeclient.ctl;

import me.hikari.snakes.SnakesProto;

import java.io.IOException;
import java.net.InetSocketAddress;

public interface Communicator {
    void sendMessage(SnakesProto.GameMessage msg, InetSocketAddress addr) throws IOException;

    void sendMessageToMaster(SnakesProto.GameMessage msg) throws IOException;

    void updateMaster(InetSocketAddress addr);
}

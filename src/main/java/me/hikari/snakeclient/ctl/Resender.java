package me.hikari.snakeclient.ctl;

import me.hikari.snakes.SnakesProto;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.function.Consumer;

interface Resender {
    void handleAck(SnakesProto.GameMessage msg, Consumer<Integer> join);

    void changeMasterInBufferedDatagrams(InetSocketAddress oldMaster, InetSocketAddress newMaster);

    void bufferSentMessage(SnakesProto.GameMessage msg, DatagramPacket packet, Long msgSeq);
}

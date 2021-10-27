package me.hikari.snakeclient.ctl;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.SneakyThrows;
import me.hikari.snakeclient.data.Peer;
import me.hikari.snakeclient.data.config.NetConfig;
import me.hikari.snakes.SnakesProto;

import java.io.IOException;
import java.net.*;

public class CommWorker implements Runnable {
    private final GameManager manager;
    private final NetConfig config;
    private final Integer port;
    private final DatagramSocket socket;

    public CommWorker(GameManager gameManager, NetConfig netConfig, Integer port) throws IOException {
        this.manager = gameManager;
        this.config = netConfig;
        this.port = port;
        socket = new DatagramSocket(port);
    }

    @SneakyThrows
    @Override
    public void run() {
        var buf = new byte[config.getMaxMsgSize()];
        while (!Thread.currentThread().isInterrupted()) {
            var packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            var socketAddress = packet.getSocketAddress();
            //TODO verify peer string creation
            var peer = new Peer(packet.getAddress().toString(), packet.getPort());
            var msg = tryDeserializeMessage(packet);
            handleMsg(msg, peer);
            sendAck(msg, socketAddress);
        }
        socket.close();
    }

    private void handleMsg(SnakesProto.GameMessage msg, Peer peer) {
        switch (msg.getTypeCase()) {
            case STEER -> handleSteer(msg, peer);
            case STATE -> handleState(msg);
            case JOIN -> handleJoin(msg);
            case ERROR -> handleError(msg);
            case ROLE_CHANGE -> handleChange(msg);
        }
    }

    private void handleSteer(SnakesProto.GameMessage msg, Peer peer) {
        manager.doSteer(msg.getSteer().getDirection(), peer);
    }
    private void handleJoin(SnakesProto.GameMessage msg) {
        //TODO implement
    }
    private void handleState(SnakesProto.GameMessage msg) {
        //TODO implement
    }
    private void handleError(SnakesProto.GameMessage msg) {
        //TODO implement
    }
    private void handleChange(SnakesProto.GameMessage msg) {
        //TODO implement
    }

    private void sendAck(SnakesProto.GameMessage msg, SocketAddress peer) throws IOException {
        if (!msg.getTypeCase().equals(SnakesProto.GameMessage.TypeCase.ACK)) {
            // TODO elaborate on ids
            var senderid = 0;
            var receiverid = 1;
            var answer = SnakesProto.GameMessage.newBuilder()
                    .setMsgSeq(msg.getMsgSeq())
                    .setAck(SnakesProto.GameMessage.AckMsg.newBuilder().build())
                    .setReceiverId(receiverid)
                    .setSenderId(senderid)
                    .build().toByteArray();
            var answerPacket = new DatagramPacket(answer, answer.length, peer);
            socket.send(answerPacket);
        }
    }

    private SnakesProto.GameMessage tryDeserializeMessage(DatagramPacket packet) throws InvalidProtocolBufferException {
        return SnakesProto.GameMessage.parseFrom(packet.getData());
    }
}

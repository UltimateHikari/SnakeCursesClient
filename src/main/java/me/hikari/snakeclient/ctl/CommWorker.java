package me.hikari.snakeclient.ctl;

import com.google.protobuf.InvalidProtocolBufferException;
import lombok.SneakyThrows;
import me.hikari.snakeclient.data.Peer;
import me.hikari.snakeclient.data.config.NetConfig;
import me.hikari.snakes.SnakesProto;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class CommWorker implements Runnable {
    private final GameManager manager;
    private final NetConfig config;
    private final Integer port;
    private final DatagramSocket socket;
    private long msg_seq = 1;

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
            try {
                socket.receive(packet);
            } catch (SocketException e) {
                // normal behaviour for call of this.close();
                return;
            }
            var msg = tryDeserializeMessage(packet);
            handleMsg(msg, packet);
        }
    }

    private Peer getPeer(DatagramPacket packet){
        return new Peer(packet.getAddress().toString(), packet.getPort());
    }

    private void handleMsg(SnakesProto.GameMessage msg, DatagramPacket packet) throws IOException {
        switch (msg.getTypeCase()) {
            case STEER -> handleSteer(msg, packet);
            case STATE -> handleState(msg, packet);
            case JOIN -> handleJoin(msg, packet);
            case ERROR -> handleError(msg);
            case ROLE_CHANGE -> handleChange(msg);
        }
    }

    private void handleSteer(SnakesProto.GameMessage msg, DatagramPacket packet) throws IOException {
        manager.doSteer(msg.getSteer().getDirection(), getPeer(packet));
        sendAck(msg, packet.getSocketAddress(), 0, 0);
    }

    private void handleJoin(SnakesProto.GameMessage msg, DatagramPacket packet) throws IOException {
        var receiverID = manager.joinPlayer(getPeer(packet), msg.getJoin().getName());
        var senderID = manager.getLocalID();
        sendAck(msg, packet.getSocketAddress(), senderID, receiverID);
    }

    private void handleState(SnakesProto.GameMessage msg, DatagramPacket packet) throws IOException {
        manager.applyState(msg.getState().getState());
        sendAck(msg, packet.getSocketAddress(), 0,0);
    }

    private void handleError(SnakesProto.GameMessage msg) {
        //TODO implement
    }

    private void handleChange(SnakesProto.GameMessage msg) {
        //TODO implement
    }

    private boolean isJoinOrChange(SnakesProto.GameMessage msg) {
        return msg.getTypeCase().equals(SnakesProto.GameMessage.TypeCase.JOIN)
                || msg.getTypeCase().equals(SnakesProto.GameMessage.TypeCase.ROLE_CHANGE);
    }

    private void sendAck(SnakesProto.GameMessage msg, SocketAddress peer, Integer senderID, Integer receiverID) throws IOException {
        var answer = SnakesProto.GameMessage.newBuilder()
                .setMsgSeq(msg.getMsgSeq())
                .setAck(SnakesProto.GameMessage.AckMsg.newBuilder().build());
        if (isJoinOrChange(msg)) {
            answer.setReceiverId(receiverID)
                    .setSenderId(senderID);
        }

        var answerBuf = answer
                .build().toByteArray();
        var answerPacket = new DatagramPacket(answerBuf, answerBuf.length, peer);
        socket.send(answerPacket);

    }

    private SnakesProto.GameMessage tryDeserializeMessage(DatagramPacket packet) throws InvalidProtocolBufferException {
        return SnakesProto.GameMessage.parseFrom(packet.getData());
    }

    public void close() {
        if (!socket.isClosed()) {
            socket.close();
        }
    }

    public void spam(SnakesProto.GameMessage.AnnouncementMsg announce) throws IOException {
        var msg = SnakesProto.GameMessage.newBuilder()
                .setAnnouncement(announce).setMsgSeq(msg_seq).build().toByteArray();
        var buf = ByteBuffer.allocate(config.getMaxMsgSize()).putInt(msg.length).put(msg).array();
        var packet = new DatagramPacket(buf, buf.length, config.getGroupAddr());
        socket.send(packet);
    }
}

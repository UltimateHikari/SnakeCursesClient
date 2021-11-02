package me.hikari.snakeclient.ctl;

import lombok.SneakyThrows;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.hikari.snakeclient.data.Peer;
import me.hikari.snakeclient.data.config.NetConfig;
import me.hikari.snakes.SnakesProto;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Log4j2
public class CommWorker implements Runnable, Communicator {
    private final Object sendLock = new Object();
    public final static Integer RESEND_TIMEOUT_MS = 20;
    private final GameManager manager;
    private final NetConfig config;
    private final DatagramSocket socket;
    private final Map<DatagramPacket, Long> datagrams = new HashMap<>();
    private Map<Long, DatagramPacket> seqs = new HashMap<>();
    private long msg_seq = 1;
    private Long joinSeq;
    private InetSocketAddress master = null;

    public CommWorker(GameManager gameManager, NetConfig netConfig, Integer port) throws IOException {
        this.manager = gameManager;
        this.config = netConfig;
        socket = new DatagramSocket(port);
    }

    @Override
    public void run() {
        var buf = new byte[config.getMaxMsgSize()];
        while (!Thread.currentThread().isInterrupted()) {
            try {
                var packet = new DatagramPacket(buf, buf.length);
                try {
                    socket.receive(packet);
                } catch (SocketException e) {
                    // normal behaviour for call of this.close();
                    return;
                }
                var msg = NetUtils.tryDeserializeGameMessage(packet);
                handleMsg(msg, packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Peer getPeer(DatagramPacket packet) {
        System.err.println("forming peer from " + packet.getAddress().getHostAddress());
        return new Peer(packet.getAddress().getHostAddress(), packet.getPort());
    }

    private void handleMsg(SnakesProto.GameMessage msg, DatagramPacket packet) throws IOException {
        switch (msg.getTypeCase()) {
            case STEER -> handleSteer(msg, packet);
            case STATE -> handleState(msg, packet);
            case JOIN -> handleJoin(msg, packet);
            case ERROR -> handleError(msg);
            case ROLE_CHANGE -> handleChange(msg);
            case ACK -> handleAck(msg);
        }
    }

    @Synchronized("sendLock")
    private void handleAck(SnakesProto.GameMessage msg) {
        // all acks matter because of join-ack
        Long confirmedSeq = msg.getMsgSeq();
        if (seqs.containsKey(confirmedSeq)) {
            datagrams.remove(seqs.remove(confirmedSeq));
            if (Objects.equals(joinSeq, confirmedSeq)) {
                manager.join(msg.getReceiverId());
            }
        }
        log.debug(confirmedSeq + ":" + seqs);
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
        updateMaster(new InetSocketAddress(packet.getAddress().getHostAddress(), packet.getPort()));
        sendAck(msg, packet.getSocketAddress(), 0, 0);
    }

    private void handleError(SnakesProto.GameMessage msg) {
        manager.handleError(msg.getError().getErrorMessage());
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

        var buf = NetUtils.serializeGameMessageBuf(answer.build().toByteArray(), config);
        var answerPacket = new DatagramPacket(buf, buf.length, peer);
        socket.send(answerPacket);
        log.info(answerPacket.getPort() + ":ACK for " + msg.getMsgSeq());

    }

    public void close() {
        if (!socket.isClosed()) {
            socket.close();
        }
    }

    public void spam(SnakesProto.GameMessage.AnnouncementMsg announce) throws IOException {
        // this MsgSeq is not bound with game and acks'll be ignored
        var msg = SnakesProto.GameMessage.newBuilder()
                .setAnnouncement(announce).setMsgSeq(msg_seq).build().toByteArray();
        var buf = NetUtils.serializeGameMessageBuf(msg, config);
        var packet = new DatagramPacket(buf, buf.length, config.getGroupAddr());
        socket.send(packet);
    }

    @Synchronized("sendLock")
    public void sendMessage(SnakesProto.GameMessage msg, InetSocketAddress addr) throws IOException {
        SnakesProto.GameMessage finalMsg = msg.toBuilder().setMsgSeq(msg_seq).build();
        var buf = NetUtils.serializeGameMessageBuf(finalMsg.toByteArray(), config);
        var packet = new DatagramPacket(buf, buf.length, addr);

        datagrams.put(packet, System.currentTimeMillis());
        seqs.put(msg_seq, packet);
        log.info(msg.getTypeCase().toString());
        if (msg.hasJoin()) {
            joinSeq = msg_seq;
        }

        msg_seq++;
        socket.send(packet);
    }

    @Override
    public void sendMessageToMaster(SnakesProto.GameMessage msg) throws IOException {
        sendMessage(msg, master);
    }

    @Override
    public void updateMaster(InetSocketAddress addr) {
        master = addr;
    }

    @Synchronized("sendLock")
    public void resend() {
        //TODO resend to new master
        var time = System.currentTimeMillis();
        datagrams.forEach((k, v) -> {
            if (time - v > RESEND_TIMEOUT_MS) {
                try {
                    resend(k, time);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void resend(DatagramPacket p, Long time) throws IOException {
        socket.send(p);
        datagrams.put(p, time);
    }
}

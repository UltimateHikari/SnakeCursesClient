package me.hikari.snakeclient.ctl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.hikari.snakeclient.data.Peer;
import me.hikari.snakeclient.data.Player;
import me.hikari.snakeclient.data.config.EngineConfig;
import me.hikari.snakeclient.data.config.NetConfig;
import me.hikari.snakes.SnakesProto;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
class NodeInfo {
    private InetSocketAddress addr;
    private Long lastSentTime;
    private Long lastRecvTime;
}


@Log4j2
class CommWorker implements Runnable, Communicator, Sender {
    private final Object nodeInfoLock = new Object();
    private final MessageDelegate manager;
    private final NetConfig config;
    private EngineConfig engineConfig;
    private final DatagramSocket socket;
    private long msg_seq = 1;
    private Resender resender;

    private NodeInfo master = null;
    private NodeInfo deputy = null;

    CommWorker(
            MessageDelegate gameManager,
            NetConfig netConfig,
            EngineConfig engineConfig,
            Integer port
    ) throws IOException {
        this.manager = gameManager;
        this.config = netConfig;
        this.engineConfig = engineConfig;
        socket = new DatagramSocket(port);
    }

    public void bindResender(Resender resendWorker) {
        this.resender = resendWorker;
    }

    /**
     * NORMAL заметил, что отвалился MASTER
     * MASTER заметил, что отвалился DEPUTY
     * DEPUTY заметил, что отвалился MASTER
     */

    @Synchronized("nodeInfoLock")
    void actualizeNodes() throws IOException {
        var time = System.currentTimeMillis();
        if (time - master.getLastRecvTime() > engineConfig.getNodeTimeoutMs()) {
            manager.masterFailed();
        } else {
            if (time - master.getLastSentTime() > engineConfig.getPingDelayMs()) {
                sendPing(master.getAddr());
                master.setLastSentTime(time);
            }
        }
        if (time - deputy.getLastRecvTime() > engineConfig.getNodeTimeoutMs()) {
            manager.deputyFailed();
        } else {
            if (time - deputy.getLastSentTime() > engineConfig.getPingDelayMs()) {
                sendPing(deputy.getAddr());
                deputy.setLastSentTime(time);
            }
        }
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

    private void handleMsg(SnakesProto.GameMessage msg, DatagramPacket packet) throws IOException {
        switch (msg.getTypeCase()) {
            case STEER -> handleSteer(msg, packet);
            case STATE -> handleState(msg, packet);
            case JOIN -> handleJoin(msg, packet);
            case ERROR -> handleError(msg);
            case ROLE_CHANGE -> handleChange(msg, packet);
            case PING -> sendAck(msg, packet);
            case ACK -> resender.handleAck(msg, manager::joinAsNormal);
        }
    }

    private void handleSteer(SnakesProto.GameMessage msg, DatagramPacket packet) throws IOException {
        manager.handleSteerMsg(msg.getSteer().getDirection(), NetUtils.getPeer(packet));
        sendAck(msg, packet);
    }

    private void handleJoin(SnakesProto.GameMessage msg, DatagramPacket packet) throws IOException {
        Integer receiverID = -1;
        try {
            receiverID = manager.handleJoinMsg(NetUtils.getPeer(packet), msg.getJoin().getName());
        } catch (IllegalStateException e) {
            log.info("Sending error: " + e.getMessage());
            var errMsg = SnakesProto.GameMessage.ErrorMsg.newBuilder()
                    .setErrorMessage(e.getMessage()).build();
            var finalErrMsg = SnakesProto.GameMessage.newBuilder().setError(errMsg).buildPartial();
            sendMessage(finalErrMsg, (InetSocketAddress) packet.getSocketAddress());
        }
        sendAckVerbose(msg, packet, receiverID);
    }

    private void handleState(SnakesProto.GameMessage msg, DatagramPacket packet) throws IOException {
        manager.handleStateMsg(msg.getState().getState());
        updateMaster(packet);
        sendAck(msg, packet);
    }

    private void handleError(SnakesProto.GameMessage msg) {
        manager.handleErrorMsg(msg.getError().getErrorMessage());
    }

    private void handleChange(SnakesProto.GameMessage msg, DatagramPacket packet) throws IOException {
        if (msg.getRoleChange().getSenderRole() == SnakesProto.NodeRole.MASTER) {
            updateMaster(packet);
        }

        if (msg.getRoleChange().getSenderRole() == SnakesProto.NodeRole.VIEWER) {
            // player voluntarily exit
            manager.handleExitChange(NetUtils.getPeer(packet));
        }
        manager.handleReceiverRoleChange(msg.getRoleChange().getReceiverRole());
        sendAck(msg, packet);
    }

    private void sendAck(SnakesProto.GameMessage msg, DatagramPacket packet) throws IOException {
        sendAckVerbose(msg, packet, manager.getPeerID(NetUtils.getPeer(packet)));
    }

    private void sendAckVerbose(
            SnakesProto.GameMessage msg,
            DatagramPacket packet,
            Integer receiverID
    ) throws IOException {
        var answer = SnakesProto.GameMessage.newBuilder()
                .setMsgSeq(msg.getMsgSeq())
                .setReceiverId(receiverID)
                .setSenderId(manager.getLocalID())
                .setAck(SnakesProto.GameMessage.AckMsg.newBuilder().build())
                .build();

        var buf = NetUtils.serializeGameMessageBuf(answer.toByteArray(), config);
        var answerPacket = new DatagramPacket(buf, buf.length, packet.getSocketAddress());
        socket.send(answerPacket);
        log.debug(answerPacket.getPort() + ":ACK for " + msg.getMsgSeq());

    }

    @Override
    public void send(DatagramPacket p) throws IOException {
        socket.send(p);
    }

    public void sendMessage(SnakesProto.GameMessage msg, InetSocketAddress addr) throws IOException {
        SnakesProto.GameMessage finalMsg = msg.toBuilder().setMsgSeq(msg_seq).build();
        var buf = NetUtils.serializeGameMessageBuf(finalMsg.toByteArray(), config);
        var packet = new DatagramPacket(buf, buf.length, addr);
        resender.bufferSentMessage(msg, packet, msg_seq);
        msg_seq++;
        socket.send(packet);
    }

    @Override
    public void sendMessageToMaster(SnakesProto.GameMessage msg) throws IOException {
        sendMessage(msg, master.getAddr());
    }

    private void sendPing(InetSocketAddress addr) throws IOException {
        var msg = SnakesProto.GameMessage.newBuilder()
                .setPing(SnakesProto.GameMessage.PingMsg.newBuilder().build())
                .buildPartial();
        sendMessage(msg, addr);
    }

    public void spam(SnakesProto.GameMessage.AnnouncementMsg announce) throws IOException {
        // this MsgSeq is not bound with game and acks'll be ignored
        var msg = SnakesProto.GameMessage.newBuilder()
                .setAnnouncement(announce).setMsgSeq(msg_seq).build().toByteArray();
        var buf = NetUtils.serializeGameMessageBuf(msg, config);
        var packet = new DatagramPacket(buf, buf.length, config.getGroupAddr());
        socket.send(packet);
    }

    private void updateMaster(DatagramPacket packet) {
        updateMaster(new InetSocketAddress(packet.getAddress().getHostAddress(), packet.getPort()));
    }

    @Override
    @Synchronized("nodeInfoLock")
    public void updateMaster(InetSocketAddress addr) {
        if(master != null) {
            resender.changeMasterInBufferedDatagrams(master.getAddr(), addr);
        }
        var time = System.currentTimeMillis();
        master = new NodeInfo(addr, time, time);
    }

    @Override
    @Synchronized("nodeInfoLock")
    public void updateDeputy(InetSocketAddress addr) {
        var time = System.currentTimeMillis();
        deputy = new NodeInfo(addr, time, time);
    }

    public void close() {
        if (!socket.isClosed()) {
            socket.close();
        }
    }
}

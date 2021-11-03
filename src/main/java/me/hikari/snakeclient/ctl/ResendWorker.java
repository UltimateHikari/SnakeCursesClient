package me.hikari.snakeclient.ctl;


import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.hikari.snakeclient.data.Peer;
import me.hikari.snakeclient.data.Player;
import me.hikari.snakeclient.data.config.EngineConfig;
import me.hikari.snakes.SnakesProto;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;


@Log4j2
class ResendWorker implements Runnable, Resender{
    private final Object buffersLock = new Object();
    public final static Integer RESEND_TIMEOUT_MS = 20;
    private final Sender communicator;
    private final Map<DatagramPacket, Long> datagrams = new HashMap<>();
    private Map<Long, DatagramPacket> seqs = new HashMap<>();
    private Long joinSeq = null;

    ResendWorker(Sender communicator){
        this.communicator = communicator;
        communicator.bindResender(this);
    }

    @Override
    @Synchronized("buffersLock")
    public void run() {
        resendAll();
    }

    @Override
    @Synchronized("buffersLock")
    public void handleAck(SnakesProto.GameMessage msg, Consumer<Integer> join) {
        Long confirmedSeq = msg.getMsgSeq();
        if (seqs.containsKey(confirmedSeq)) {
            datagrams.remove(seqs.remove(confirmedSeq));
            if (Objects.equals(joinSeq, confirmedSeq)) {
                join.accept(msg.getReceiverId());
            }
        }
        log.debug(confirmedSeq + ":" + seqs);
    }

    public void resendAll() {
        var time = System.currentTimeMillis();
        datagrams.forEach((k, v) -> {
            if (time - v > RESEND_TIMEOUT_MS) {
                try {
                    resend(k);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    @Synchronized("buffersLock")
    public void changeMasterInBufferedDatagrams(InetSocketAddress oldMaster, InetSocketAddress newMaster) {
        // TODO: stub
    }

    @Override
    public void bufferSentMessage(SnakesProto.GameMessage msg, DatagramPacket packet, Long msgSeq) {
        datagrams.put(packet, System.currentTimeMillis());
        seqs.put(msgSeq, packet);
        log.debug("buffered " + msg.getTypeCase());
        if (msg.hasJoin()) {
            joinSeq = msgSeq;
        }
    }

    @Synchronized("buffersLock")
    private void resend(DatagramPacket p) throws IOException {
        communicator.send(p);
    }
}

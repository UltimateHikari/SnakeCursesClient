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
class ResendWorker implements Runnable, Resender {
    private static final Long MAX_RESENDS = 3L;
    private final Object buffersLock = new Object();
    public final static Integer RESEND_TIMEOUT_MS = 20;
    private final Sender communicator;
    private final Map<DatagramPacket, Long> datagrams = new HashMap<>();
    private Map<Long, DatagramPacket> seqs = new HashMap<>();
    private Long joinSeq = null;

    ResendWorker(Sender communicator) {
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
        datagrams.forEach((k, v) -> {
            try {
                if (v < MAX_RESENDS) {
                    resend(k);

                } else {
                    communicator.noteFailed(k);
                }
            } catch (IOException e) {
                log.error(e.getStackTrace());
            }
        });
    }

    @Override
    @Synchronized("buffersLock")
    public void changeMasterInBufferedDatagrams(InetSocketAddress oldMaster, InetSocketAddress newMaster) {
        datagrams.forEach((k, v) -> {
            if (k.getSocketAddress().equals(oldMaster)) {
                k.setSocketAddress(newMaster);
            }
        });
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

    private void resend(DatagramPacket p) throws IOException {
        communicator.send(p);
    }
}

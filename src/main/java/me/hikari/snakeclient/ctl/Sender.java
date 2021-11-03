package me.hikari.snakeclient.ctl;

import java.io.IOException;
import java.net.DatagramPacket;

/**
 * CommWorker as seen from Resender perspective
 */

interface Sender {
    void bindResender(Resender resendWorker);

    void send(DatagramPacket p) throws IOException;
}

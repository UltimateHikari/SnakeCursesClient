package me.hikari.snakeclient.ctl;

import com.sun.source.tree.NewArrayTree;

import java.io.IOException;
import java.net.*;

class ListenWorker implements Runnable{
    private final InetSocketAddress group;
    private MulticastSocket socket;
    private NetworkInterface netIf;
    private final GameManager manager;

    public ListenWorker(GameManager manager, InetSocketAddress group, Integer port) throws IOException {
        this.manager = manager;
        this.group = group;
        //todo move outside
        this.netIf = NetworkInterface.getByName("wlan0");
//        socket = new MulticastSocket(port);
//        socket.joinGroup(group);
//
//        socket = new MulticastSocket(InetAddress.getByName(addr));
//
//        s.joinGroup(group, netIf);
//        byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
//        DatagramPacket hi = new DatagramPacket(msgBytes, msgBytes.length,
//                group, 6789);
//        s.send(hi);
//        // get their responses!
//        byte[] buf = new byte[1000];
//        DatagramPacket recv = new DatagramPacket(buf, buf.length);
//        s.receive(recv);
//        // OK, I'm done talking - leave the group...
//        s.leaveGroup(group, netIf);
    }

    @Override
    public void run() {

    }
}

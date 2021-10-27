package me.hikari.snakeclient.data.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.net.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class NetConfig {
    private Integer maxMsgSize;
    private String groupIP;
    private Integer groupPort;
    private String groupIf;
    private Integer listenPort;

    public NetworkInterface getNetIf() throws SocketException {
        return NetworkInterface.getByName(groupIf);
    }

    public SocketAddress getGroupAddr() {
        return new InetSocketAddress(groupIP, groupPort);
    }
}

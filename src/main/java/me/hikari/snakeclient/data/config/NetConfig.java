package me.hikari.snakeclient.data.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.net.*;

@Getter
public class NetConfig {
    public static final int MAX_MESSAGE_SIZE = 1000;
    private final InetSocketAddress groupAddr;
    private final NetworkInterface netIf;
    private final Integer listenPort;

    @JsonCreator
    public NetConfig(
            @JsonProperty("group_ip") String groupIP,
            @JsonProperty("group_port") Integer groupPort,
            @JsonProperty("group_iface") String groupIf,
            @JsonProperty("listen_port") Integer listenPort
    ) throws UnknownHostException, SocketException {
        this.groupAddr = new InetSocketAddress(InetAddress.getByName(groupIP), groupPort);
        this.netIf = NetworkInterface.getByName(groupIf);
        this.listenPort = listenPort;
    }

    @Override
    public String toString() {
        return "{" + groupAddr + ":" + netIf + "; " + listenPort + "}";
    }
}

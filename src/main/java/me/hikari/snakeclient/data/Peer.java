package me.hikari.snakeclient.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Peer {
    private final String ip;
    private final Integer port;

    public boolean equals(Player p){
        return this.ip == p.getIp() && this.port == p.getPort();
    }
}

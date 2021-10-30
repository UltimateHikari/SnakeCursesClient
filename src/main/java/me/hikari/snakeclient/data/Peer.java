package me.hikari.snakeclient.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class Peer {
    private final String ip;
    private final Integer port;

    public boolean equals(Player p){
        return Objects.equals(this.ip, p.getIp()) && Objects.equals(this.port, p.getPort());
    }
}

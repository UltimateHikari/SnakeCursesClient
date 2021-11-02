package me.hikari.snakeclient.data;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import me.hikari.snakeclient.data.config.PlayerConfig;
import me.hikari.snakes.SnakesProto;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class Player {
    private final String name;
    private Integer id;
    private final String ip;
    private final Integer port;
    @Setter
    private SnakesProto.NodeRole role;
    @EqualsAndHashCode.Exclude
    private Integer score;

    public Player(PlayerConfig config) {
        this(
                config.getName(),
                PlayerConfig.ID,
                PlayerConfig.IP,
                config.getPort(),
                PlayerConfig.ROLE,
                PlayerConfig.SCORE
        );
    }

    public Player(SnakesProto.GamePlayer player) {
        this(
                player.getName(),
                player.getId(),
                player.getIpAddress(),
                player.getPort(),
                player.getRole(),
                player.getScore()
        );
    }

    public Player(Peer peer, String name, Integer id) {
        this(
                name,
                id,
                peer.getIp(),
                peer.getPort(),
                SnakesProto.NodeRole.NORMAL,
                PlayerConfig.SCORE
        );
    }

    void score() {
        score += 1;
    }

    public boolean isMaster() {
        return this.role == SnakesProto.NodeRole.MASTER;
    }

    public void reset() {
        this.id = 0;
        this.role = SnakesProto.NodeRole.MASTER;
        this.score = 0;
    }

    public SnakesProto.GamePlayer retrieve() {
        return SnakesProto.GamePlayer.newBuilder()
                .setName(name)
                .setId(id)
                .setIpAddress(ip)
                .setPort(port)
                .setRole(role)
                .setScore(score)
                .build();
    }

    public void become(Integer receiverID) {
        id = receiverID;
    }
}

package me.hikari.snakeclient.data;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.hikari.snakeclient.data.config.PlayerConfig;
import me.hikari.snakes.SnakesProto;

import java.util.Objects;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class Player {
    private final String name;
    private Integer id;
    private final String ip;
    private final Integer port;
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

    void score() {
        score += 1;
    }

    public boolean isMaster() {
        return this.role == SnakesProto.NodeRole.MASTER;
    }

    public void reset(){
        this.id = 0;
        this.role = SnakesProto.NodeRole.MASTER;
        this.score = 0;
    }

    void changeRole(SnakesProto.NodeRole role){
        this.role = role;
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
}

package me.hikari.snakeclient.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.hikari.snakeclient.data.config.PlayerConfig;
import me.hikari.snakes.SnakesProto;

@AllArgsConstructor
@Getter
public class Player {
    private final String name;
    private final Integer id;
    private final String ip;
    private final Integer port;
    private final SnakesProto.NodeRole role;
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

    public void score() {
        score += 1;
    }

    public boolean isHost() {
        return this.role == SnakesProto.NodeRole.MASTER;
    }
}

package me.hikari.snakeclient.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.hikari.snakeclient.data.config.PlayerConfig;
import me.hikari.snakes.SnakesProto;

@RequiredArgsConstructor
@Getter
public class Player {
    private final String name;
    private final Integer id;
    private final Integer port;
    private final String ip;
    private Integer score = 0;

    public Player(PlayerConfig config) {
        this(config.getName(), PlayerConfig.MASTER_ID, config.getPort(), PlayerConfig.MASTER_IP);
    }

    public Player(SnakesProto.GamePlayer player){
        this(player.getName(), player.getId(), player.getPort(), player.getIpAddress());
    }

    public void score() {
        score += 1;
    }
}

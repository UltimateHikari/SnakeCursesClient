package me.hikari.snakeclient.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.hikari.snakeclient.data.config.PlayerConfig;

@RequiredArgsConstructor
@Getter
public class Player {
    private final String name;
    private final Integer id;
    private final Integer port;
    private final String ip;
    private Integer score = 0;
    //TODO deprecate this constructor
    public Player(){
        this("host", 0, 8080,"localhost");
    }
    public Player(PlayerConfig config){
        this(config.getName(), PlayerConfig.MASTER_ID, config.getPort(), PlayerConfig.MASTER_IP);
    }
    public void score(){
        score += 1;
    }
}

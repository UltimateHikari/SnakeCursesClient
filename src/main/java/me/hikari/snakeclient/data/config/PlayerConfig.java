package me.hikari.snakeclient.data.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import me.hikari.snakes.SnakesProto;

/**
 * Config for initializing master node
 * to start the game
 */

@Getter
@ToString
public class PlayerConfig {
    private final String name;
    public static final Integer ID = 0;
    public static final String IP = "";
    private final Integer port;
    public static final Integer SCORE = 0;
    public static final SnakesProto.NodeRole ROLE = SnakesProto.NodeRole.MASTER;

    @JsonCreator
    public PlayerConfig(
            @JsonProperty("name") String name,
            @JsonProperty("port") Integer port
    ){
        this.name = name;
        this.port = port;
    }
}

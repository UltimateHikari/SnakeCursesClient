package me.hikari.snakeclient.data.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PlayerConfig {
    public static final Integer MASTER_ID = 0;
    public static final String MASTER_IP = "";
    private final String name;
    private final Integer port;

    @JsonCreator
    public PlayerConfig(
            @JsonProperty("name") String name,
            @JsonProperty("port") Integer port
    ){
        this.name = name;
        this.port = port;
    }
}

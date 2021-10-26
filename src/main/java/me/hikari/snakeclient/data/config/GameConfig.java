package me.hikari.snakeclient.data.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GameConfig {
    private final PlayerConfig playerConfig;
    private final NetConfig netConfig;
    //TODO accept EngineConfig []
    private final EngineConfig engineConfig;
    private final KeyConfig keyConfig;

    public GameConfig(
            @JsonProperty("player") PlayerConfig playerConfig,
            @JsonProperty("net") NetConfig netConfig,
            @JsonProperty("engine") EngineConfig engineConfig,
            @JsonProperty("keys") KeyConfig keyConfig
    ){
        this.playerConfig = playerConfig;
        this.netConfig = netConfig;
        this.engineConfig = engineConfig;
        this.keyConfig = keyConfig;
    }
}

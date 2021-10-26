package me.hikari.snakeclient.data.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonKey;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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

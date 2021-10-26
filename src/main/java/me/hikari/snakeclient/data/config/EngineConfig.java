package me.hikari.snakeclient.data.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.hikari.snakeclient.data.Coord;
import me.hikari.snakeclient.data.UIConfig;

@Getter
@RequiredArgsConstructor
public class EngineConfig implements UIConfig {
    private final Coord worldSize;
    private final Integer foodStatic;
    private final Float foodPerPlayer;
    private final Integer stateDelayMs;
    private final Float deadFoodProb;
    private final Integer pingDelayMs;
    private final Integer nodeTimeoutMs;

    @JsonCreator
    public EngineConfig(
            @JsonProperty("width") Integer width,
            @JsonProperty("height") Integer height,
            @JsonProperty("food_static") Integer foodStatic,
            @JsonProperty("food_per_player") Float foodPerPlayer,
            @JsonProperty("state_delay_ms") Integer stateDelayMs,
            @JsonProperty("dead_food_prob") Float deadFoodProb,
            @JsonProperty("ping_delay_ms") Integer pingDelayMs,
            @JsonProperty("node_timeout_ms") Integer nodeTimeoutMs) {
        this(
                new Coord(width, height),
                foodStatic,
                foodPerPlayer,
                stateDelayMs,
                deadFoodProb,
                pingDelayMs,
                nodeTimeoutMs);
    }

    @Override
    public String toString(){
        return "{" + worldSize + ", " + foodStatic
                + "x" + foodPerPlayer + ", "
                + stateDelayMs + ", " + deadFoodProb + ", "
                + pingDelayMs + "x" + nodeTimeoutMs + "}";
    }
}

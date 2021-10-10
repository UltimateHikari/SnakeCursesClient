package me.hikari.snakeclient.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EngineConfig {
    private final Integer width;
    private final Integer height;
    private final Integer foodStatic;
    private final Float foodPerPlayer;
    private final Integer stateDelayMs;
    private final Float deadFoodProb;
    private final Integer pingDelayMs;
    private final Integer nodeTimeoutMs;
    public EngineConfig(){
        this(40,30,1,1.f,1000,.1f,100,800);
    }
}

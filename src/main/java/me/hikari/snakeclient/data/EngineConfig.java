package me.hikari.snakeclient.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EngineConfig implements UIConfig {
    private final Coord worldSize;
    private final Integer foodStatic;
    private final Float foodPerPlayer;
    private final Integer stateDelayMs;
    private final Float deadFoodProb;
    private final Integer pingDelayMs;
    private final Integer nodeTimeoutMs;
    public EngineConfig(){
        this(new Coord(20, 10),1,1.f,100,.1f,100,800);
    }
}

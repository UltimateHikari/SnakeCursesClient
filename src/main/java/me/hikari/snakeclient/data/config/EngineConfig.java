package me.hikari.snakeclient.data.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.hikari.snakeclient.data.Coord;

@AllArgsConstructor
@Getter
public class EngineConfig implements UIConfig{
    private final Integer width;
    private final Integer height;
    private final Integer foodStatic;
    private final Float foodPerPlayer;
    private final Integer stateDelayMs;
    private final Float deadFoodProb;
    private final Integer pingDelayMs;
    private final Integer nodeTimeoutMs;
    //TODO check for differrent w/h
    public EngineConfig(){
        this(20,20,1,1.f,100,.1f,100,800);
    }
    public Coord worldSize(){
        return new Coord(width, height);
    }
}

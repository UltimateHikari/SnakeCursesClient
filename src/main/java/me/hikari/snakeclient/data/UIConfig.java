package me.hikari.snakeclient.data;

public interface UIConfig {
    Integer getStateDelayMs();
    Integer getFoodStatic();
    Float getFoodPerPlayer();
    Float getDeadFoodProb();
    Coord getWorldSize();
}

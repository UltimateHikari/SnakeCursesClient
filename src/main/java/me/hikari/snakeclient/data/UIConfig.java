package me.hikari.snakeclient.data;

import me.hikari.snakeclient.data.Coord;

public interface UIConfig {
    Integer getStateDelayMs();
    Integer getFoodStatic();
    Float getFoodPerPlayer();
    Float getDeadFoodProb();
    Coord getWorldSize();
}

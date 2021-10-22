package me.hikari.snakeclient.data.config;

import me.hikari.snakeclient.data.Coord;

public interface UIConfig {
    Integer getWidth();
    Integer getHeight();
    Integer getStateDelayMs();
    Coord worldSize();
}

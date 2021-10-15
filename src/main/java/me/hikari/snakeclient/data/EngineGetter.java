package me.hikari.snakeclient.data;

import me.hikari.snakeclient.data.config.UIConfig;

import java.util.Map;

public interface EngineGetter {
    Map<Player, Snake> getSnakeMap();
    UIConfig getUIConfig();
}

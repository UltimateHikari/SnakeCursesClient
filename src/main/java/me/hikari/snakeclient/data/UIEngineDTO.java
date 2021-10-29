package me.hikari.snakeclient.data;

import java.util.List;

public interface UIEngineDTO {
    UIConfig getConfig();
    List<UISnake> getSnakes();
    List<Player> getPlayers();
    List<Coord> getFoods();
}

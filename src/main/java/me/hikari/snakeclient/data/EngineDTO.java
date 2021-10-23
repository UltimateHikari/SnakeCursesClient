package me.hikari.snakeclient.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class EngineDTO {
    private final Map<Player, UISnake> snakeMap;
    private final List<Coord> foods;
    private final UIConfig uiConfig;
}

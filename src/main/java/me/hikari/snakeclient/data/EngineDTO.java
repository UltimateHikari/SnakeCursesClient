package me.hikari.snakeclient.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


//TODO retrieve state constructor
@Getter
@AllArgsConstructor
public class EngineDTO implements UIEngineDTO{
    private final Integer stateOrder;
    private final List<UISnake> snakes;
    private final List<Player> players;
    private final List<Coord> foods;
    private final UIConfig config;

//    public SnakesProto.GameMessage.StateMsg retrieveState(){
//        return null;
//    }
}

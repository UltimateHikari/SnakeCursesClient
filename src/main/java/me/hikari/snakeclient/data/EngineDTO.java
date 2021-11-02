package me.hikari.snakeclient.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.hikari.snakeclient.data.config.EngineConfig;
import me.hikari.snakes.SnakesProto;

import java.util.List;


@Getter
@AllArgsConstructor
public class EngineDTO implements UIEngineDTO{
    private final Integer stateOrder;
    private final List<UISnake> snakes;
    private final List<Player> players;
    private final List<Coord> foods;
    private final UIConfig config;
    private final String error;

    private SnakesProto.GamePlayers retrievePlayers(){
        return SnakesProto.GamePlayers.newBuilder()
                .addAllPlayers(players.stream().map(Player::retrieve).toList())
                .build();
    }

    public SnakesProto.GameMessage.AnnouncementMsg retrieveAnnouncement(){
        return SnakesProto.GameMessage.AnnouncementMsg.newBuilder()
                .setPlayers(retrievePlayers())
                .setConfig(((EngineConfig)config).retrieve())
                .build();
    }

    private SnakesProto.GameState getState(){
        return SnakesProto.GameState
                .newBuilder()
                .setStateOrder(stateOrder)
                .addAllSnakes(snakes.stream().map(s -> ((Snake)s).retrieve()).toList())
                .addAllFoods(foods.stream().map(Coord::retrieve).toList())
                .setPlayers(retrievePlayers())
                .setConfig(((EngineConfig)config).retrieve())
                .build();
    }

    public SnakesProto.GameMessage.StateMsg retrieveState(){
        return SnakesProto.GameMessage.StateMsg.newBuilder().setState(getState()).build();
    }
}

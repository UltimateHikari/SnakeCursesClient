package me.hikari.snakeclient.data;

import me.hikari.snakeclient.data.config.EngineConfig;
import me.hikari.snakeclient.data.config.UIConfig;

import java.util.ArrayList;
import java.util.List;

public class Engine {
    private Integer stateOrder;
    private EngineConfig config;
    //TODO list -> map
    private List<Snake> snakes = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    private List<Coord> foods = new ArrayList<>();

    public Engine(EngineConfig config){
        this.config = config;
    }
    public void addPlayer(Player player){
        players.add(player);
    }

    public void notePlayerMove(int id, Direction move){
    }

    public UIConfig getUIConfig(){
        return config;
    }

    public void applyMoves() {
    }

    public void moveSnakes() {
    }
}
